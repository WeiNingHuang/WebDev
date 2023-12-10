package com.example.questionnaire.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.questionnaire.constants.RtnCode;
import com.example.questionnaire.entity.Question;
import com.example.questionnaire.entity.Questionnaire;
import com.example.questionnaire.repository.QuestionDao;
import com.example.questionnaire.repository.QuestionnaireDao;
import com.example.questionnaire.service.ifs.QuizService;
import com.example.questionnaire.vo.QnQuVo;
import com.example.questionnaire.vo.QuestionRes;
import com.example.questionnaire.vo.QuestionnaireRes;
import com.example.questionnaire.vo.QuizReq;
import com.example.questionnaire.vo.QuizRes;
import com.example.questionnaire.vo.QuizVo;

@Service
public class QuizServiceImpl implements QuizService {

	@Autowired
	private QuestionnaireDao qnDao;

	@Autowired
	private QuestionDao quDao;

//	新增問卷及題目
	@Transactional // 交易，概念為一張表存多筆資料或跨表儲存資料時，僅有全成功進DB或全失敗皆不進DB。只能用於public方法上，private不行。可直接放於class上。
	@Override
	public QuizRes create(QuizReq req) {
		// 檢查參數
		QuizRes checkResult = checkParam(req);
		if (checkResult != null) {
			return checkResult;
		}
		// 問卷存進DB
		int quId = qnDao.save(req.getQuestionnaire()).getId();
		// 題目為空時(尚未有題目) 參數檢查方法內的題目參數檢查(for迴圈)就不會執行
		List<Question> quList = req.getQuestionList();
		if (quList.isEmpty()) {
			return new QuizRes(RtnCode.SUCCESSFUL);
		}
		// 不為空時(有題目)，再執行以下操作 -> 取得問卷流水號(set給題目)
		for (Question qu : quList) {
			qu.setQnId(quId);
		}
		// 題目存進DB
		quDao.saveAll(quList);
		return new QuizRes(RtnCode.SUCCESSFUL);
	}

//	編輯問卷及題目
	@Transactional
	@Override
	public QuizRes update(QuizReq req) {
		// 檢查參數
		QuizRes checkResult = checkParam(req);
		if (checkResult != null) {
			return checkResult;
		}
		// 檢查參數-問卷流水號
		checkResult = checkQnId(req);
		if (checkResult != null) {
			return checkResult;
		}
		// 檢查資料是否已存在 (因JPA save方法，id為PK，其不存在即新增)
		// 因為後續還要進行操作而直接使用findById，existsById僅回傳布林值
//		if (!qnDao.existsById(req.getQuestionnaire().getId())) {
//			return new QuizRes(RtnCode.QUESTIONNAIRE_ID_NOT_FOUND);
//		}
		Optional<Questionnaire> qnOp = qnDao.findById(req.getQuestionnaire().getId());
		if (qnOp.isEmpty()) {
			return new QuizRes(RtnCode.QUESTIONNAIRE_ID_NOT_FOUND);
		}
		Questionnaire qn = qnOp.get();
		// 可以進行編輯的條件
		// 1.尚未發布： is_published == false 可編輯
		// 2.已發布但尚未開始進行：is_published == true ＋ 當前時間須小於start_date
		if (!qn.isPublished() || (qn.isPublished() && LocalDate.now().isBefore(qn.getStartDate()))) {
			qnDao.save(req.getQuestionnaire());
			quDao.saveAll(req.getQuestionList());
			return new QuizRes(RtnCode.SUCCESSFUL);
		}
		return new QuizRes(RtnCode.UPDATE_ERROR);
	}

//	刪除問卷
	@Transactional
	@Override
	public QuizRes deleteQuestionnaire(List<Integer> qnIdList) {
		// findByIdIn不需檢查<= 0或null，因為有就有，沒有找到就沒有。
		List<Questionnaire> qnList = qnDao.findByIdIn(qnIdList);
		List<Integer> idList = new ArrayList<>();
		// 符合條件就刪除
		for (Questionnaire qn : qnList) {
			if (!qn.isPublished() || qn.isPublished() && LocalDate.now().isBefore(qn.getStartDate())) {
//				qnDao.deleteById(qn.getId());這樣寫法會每找到一筆符合就進資料庫，不建議！
				idList.add(qn.getId());
			}
		}
		// 符合條件不為空時，才連線資料庫進行刪除問卷，並且刪除問卷內的題目
		if (!idList.isEmpty()) {
			qnDao.deleteAllById(idList);
			quDao.deleteAllByQnIdIn(idList);
		}
		return new QuizRes(RtnCode.SUCCESSFUL);
	}

//	刪除題目
	@Transactional
	@Override
	public QuizRes deleteQuestion(int qnId, List<Integer> quIdList) {
		// 找問卷流水號
		Optional<Questionnaire> qnOp = qnDao.findById(qnId);
		// 問卷流水號為空時，表示並不會進行刪除 不存在的問卷
		if (qnOp.isEmpty()) {
			return new QuizRes(RtnCode.SUCCESSFUL);
		}
		// 問卷存在時檢查是否符合條件，1.尚未發布、2.已發布但尚未開始進行，才可進行題目刪除
		Questionnaire qn = qnOp.get();
		if (!qn.isPublished() || qn.isPublished() && LocalDate.now().isBefore(qn.getStartDate())) {
			quDao.deleteAllByQnIdAndQuIdIn(qnId, quIdList);

//			return new QuizRes(RtnCode.SUCCESSFUL);
		}
		return new QuizRes(RtnCode.SUCCESSFUL);
	}

//	模糊搜尋方法1
	@Cacheable(cacheNames = "search",
			// key值 =#title_#startDate_#endDate"
			// 底線串接，key ="test_2023-11-10_2023-11-31"
			key = "#title.concat('_').concat(#startDate.toString()).concat('_').concat(#endDate.toString())", unless = "#result.rtnCode.code != 200")
	@Override
	public QuizRes search(String title, LocalDate startDate, LocalDate endDate) {
		// 搜尋條件 (為配合＠Cacheable，toString()不能為null，可以為空字串，故移至Controller先進行判斷
//		title = StringUtils.hasText(title) ? title : ""; //方法含Containing，當設字串帶""時，才會帶全部資料。等於在搜尋標題欄位沒有輸入東西時，也可帶出所有資料。
//		startDate = startDate != null ? startDate : LocalDate.of(1971, 1, 1); //設一個系統開始時間及超晚的時間，使用者搜尋時間才能落在此區間內
//		endDate = endDate != null ? endDate : LocalDate.of(2099, 12, 31);

		// 撈出搜尋後的問卷列表(qnList：問卷清單，內含有符合搜尋條件之問卷,不含題目)
		List<Questionnaire> qnList = qnDao
				.findByTitleContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqual(title, startDate, endDate);
		// 同時撈出問卷內對應的題目
		List<Integer> qnIds = new ArrayList<>(); // qnIds:問卷流水號清單，存放符合條件問卷的流水號
		for (Questionnaire qu : qnList) {
			qnIds.add(qu.getId()); // 對qnList遍歷，來取得其問卷的流水號，放入List(qnIds):存放符合條件問卷的流水號
		}
		List<Question> quList = quDao.findAllByQnIdIn(qnIds); // qnIds為空時會報錯。依據題目內的問卷流水號QnId，放入List(quList)：quList內含資料庫所有題目內的問卷流水號QnId
//		List<Question> quList = new ArrayList<>();
//		if(!qnIds.isEmpty()) {
//			quList = quDao.findAllByQnIdIn(qnIds);
//		}
		List<QuizVo> quizVoList = new ArrayList<>();
		// 問卷與題目配對
		for (Questionnaire qn : qnList) { // 對符合搜尋條件之問卷(qnList)遍歷，為了分別將每張問卷分別裝入各自的vo(組合)
			QuizVo vo = new QuizVo(); // 配對完裝回去QuizVo，問卷有幾張，就有幾個Vo
			vo.setQuestionnaire(qn);
			List<Question> questionList = new ArrayList<>();
			for (Question qu : quList) { // 對quList(內含資料庫所有題目內的問卷流水號QnId)遍歷，為了比對
				if (qu.getQnId() == qn.getId()) {
					questionList.add(qu); //// 比對題目的問卷流水號與符合問卷的流水號相同時，將符合之題目放入questionList
				}
			}
			vo.setQuestionList(questionList); // 比對後的題目清單set回vo
			quizVoList.add(vo);
		}
		return new QuizRes(quizVoList, RtnCode.SUCCESSFUL);
	}

//	模糊搜尋方法2 (分開寫版本-問卷）
	@Override
	public QuestionnaireRes searchQuestionnaireList(String title, LocalDate startDate, LocalDate endDate,
			boolean isPublished) {
		// 搜尋條件
//		title = StringUtils.hasText(title) ? title : "";
//		startDate = startDate != null ? startDate : LocalDate.of(1971, 1, 1);
//		endDate = endDate != null ? endDate : LocalDate.of(2099, 12, 31);
		List<Questionnaire> qnList = new ArrayList<>();
		if (isPublished) {
			qnList = qnDao.findByTitleContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqualAndPublishedTrue(
					title, startDate, endDate); // 給前端
			return new QuestionnaireRes(qnList, RtnCode.SUCCESSFUL);
		} else {
			qnList = qnDao.findByTitleContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqual(title, startDate,
					endDate); // 給後端
			return new QuestionnaireRes(qnList, RtnCode.SUCCESSFUL);
		}
	}

//	模糊搜尋方法2 (分開寫版本-題目）
	@Override
	public QuestionRes searchQuestionList(int qnId) {
		if (qnId <= 0) {
			return new QuestionRes(null, RtnCode.QUESTIONNAIRE_ID_PARAM_ERROR);
		}
		List<Question> quList = quDao.findAllByQnIdIn(Arrays.asList(qnId)); // 或是至QuestionDao再建一個方法public List<Question>
																			// findAllByQnId(int qnId)
		return new QuestionRes(quList, RtnCode.SUCCESSFUL);
	}
	
	//使用SQL
	@Override
	public QuizRes selectFuzzy(String title, LocalDate startDate, LocalDate endDate) {
		List<QnQuVo> res = qnDao.selectFuzzy(title, startDate, endDate);
		return new QuizRes(null, res, RtnCode.SUCCESSFUL);
	}
	
//===================================================================================================
//	檢查參數的方法 (抽出來定義一個私有方法)(回傳兩種型態null:檢查成功 或 QuizRes:沒有東西->RtnCode)
	private QuizRes checkParam(QuizReq req) {
		// 問卷參數檢查
		Questionnaire qn = req.getQuestionnaire();
		if (!StringUtils.hasText(qn.getTitle()) || !StringUtils.hasText(qn.getDescription())
				|| qn.getStartDate() == null || qn.getEndDate() == null || qn.getStartDate().isAfter(qn.getEndDate())) {
			return new QuizRes(RtnCode.QUESTIONNAIRE_PARAM_ERROR);
		}

		// 題目參數檢查 getQnId()不用檢查是因還未生成，故無法帶入
		// 空陣列(尚未有題目）for迴圈不會執行
		List<Question> quList = req.getQuestionList();
		for (Question qu : quList) {
			if (qu.getQuId() <= 0 || !StringUtils.hasText(qu.getQnTitle()) || !StringUtils.hasText(qu.getOptionType())
					|| !StringUtils.hasText(qu.getOption())) {
				return new QuizRes(RtnCode.QUESTION_PARAM_ERROR);
			}
		}
		return null;
	}

//  檢查參數-問卷流水號的方法
	private QuizRes checkQnId(QuizReq req) {
		// 檢查參數-問卷流水號。
		if (req.getQuestionnaire().getId() <= 0) {
			return new QuizRes(RtnCode.QUESTIONNAIRE_ID_PARAM_ERROR);
		}
		// 問卷流水號是否與題目其對應的qnId相同。問卷的id因創建後才有Ai生成之流水號，題目才能get qnId，故在更新(編輯題目)時，也需檢查。
		List<Question> quList = req.getQuestionList();
		for (Question qu : quList) {
			if (qu.getQnId() != req.getQuestionnaire().getId()) {
				return new QuizRes(RtnCode.QUESTIONNAIRE_ID_PARAM_ERROR);
			}
		}
		return null;
	}

}
