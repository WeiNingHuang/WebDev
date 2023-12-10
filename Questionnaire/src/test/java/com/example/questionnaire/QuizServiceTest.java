package com.example.questionnaire;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import com.example.questionnaire.entity.Question;
import com.example.questionnaire.entity.Questionnaire;
import com.example.questionnaire.repository.QuestionnaireDao;
import com.example.questionnaire.service.ifs.QuizService;
import com.example.questionnaire.vo.QnQuVo;
import com.example.questionnaire.vo.QuizReq;
import com.example.questionnaire.vo.QuizRes;

@SpringBootTest
public class QuizServiceTest {

	@Autowired
	private QuizService service;
	
	@Autowired
	private QuestionnaireDao qnDao;
	
	@Test
	public void creatTest(){
		Questionnaire questionnaire = new Questionnaire("test1", "test", false,
				LocalDate.of(2023, 11, 17),  LocalDate.of(2023, 11, 30));
		
		List<Question> questionList = new ArrayList<>();
		Question q1 = new Question(1, "test_quesition_1", "single", false, "AAA;BBB;CCC");
		Question q2 = new Question(2, "test_quesition_2", "multi", false, "10;20;30;40");
		Question q3 = new Question(3, "test_quesition_3", "text", false, "ABC");
		questionList.addAll(Arrays.asList(q1, q2, q3));
		
		QuizReq req = new QuizReq(questionnaire, questionList);
		QuizRes res = service.create(req);
		Assert.isTrue(res.getRtnCode().getCode() == 200, "create error!");
	}
	
	@Test
	public void updateTest(){
		Questionnaire questionnaire = new Questionnaire(5, "test2", "test", false,
				LocalDate.of(2023, 12, 17),  LocalDate.of(2023, 12, 30)); //改問卷標題、問卷時間
		List<Question> questionList = new ArrayList<>();
		Question q3 = new Question(3, 5, "test_quesition_3", "text", false, "DEF"); //修改題目
		Question q4 = new Question(4, 5, "test_quesition_4", "single", false, "DDD;EEE"); //新增題目
		questionList.addAll(Arrays.asList(q3, q4));
		QuizReq req = new QuizReq(questionnaire, questionList);
		QuizRes res = service.update(req);
		Assert.isTrue(res.getRtnCode().getCode() == 200, "update error!");
	}
	
	@Test
	public void deleteQuestionnaireTest(){
//		List<Integer> qnIdList = new ArrayList<>(Arrays.asList(5, 6));還沒寫完
		
	}
	
//================================== SQL語法測試 ====================================================
	@Test
	public void insertTest(){
		int res = qnDao.insert("qa_01", "qa_01 test", false, LocalDate.of(2023, 11, 24), LocalDate.of(2024, 01, 02));
		System.out.println(res);
	}
	
	@Test
	public void insertDataTest(){
		int res = qnDao.insertDate("qa_02", "qa_01 test", false, LocalDate.of(2023, 11, 24), LocalDate.of(2024, 01, 02));
		System.out.println(res);
	}
	
	@Test
	public void updateTest1(){
		int res = qnDao.update(12, "qn_007", "qn_007_test"); 
		System.out.println(res); //印出1，若找不到id，則印出0
	}
	
	@Test
	public void updateDataTest(){
		int res = qnDao.updateData(13, "qn_099", "qn_099_test", LocalDate.of(2023, 11, 20)); 
		System.out.println(res);
	}
	
	@Test
	public void selectTest(){
//		List<Questionnaire> res = qnDao.findByStartDate(LocalDate.of(2023, 11, 20)); 
//		List<Questionnaire> res = qnDao.findByStartDate1(LocalDate.of(2023, 11, 20)); 
//		List<Questionnaire> res = qnDao.findByStartDate2(LocalDate.of(2023, 11, 20)); 
//		List<Questionnaire> res = qnDao.findByStartDate3(LocalDate.of(2023, 11, 20), true);
//		List<Questionnaire> res = qnDao.findByStartDate4(LocalDate.of(2023, 11, 20), false);
		List<Questionnaire> res = qnDao.findByStartDate5(LocalDate.of(2023, 11, 20), false, 2);
		System.out.println(res.size());
	}
	
	@Test
	public void limitTest() {
		List<Questionnaire> res = qnDao.findWithLimitAndStartIndex(1, 3);
//		for(Questionnaire item : res) {
//			System.out.println(item.getId());
//		}
		res.forEach(item ->{  //lamda寫法
			System.out.println(item.getId());
		});
	}
	
	@Test
	public void likeTest() {
//		List<Questionnaire> res = qnDao.searchTitleLike("test");
		List<Questionnaire> res = qnDao.searchTitleLike2("test");
		res.forEach(item ->{
			System.out.println(item.getTitle());
		});
	}
	
	@Test
	public void regexpTest() {
//		List<Questionnaire> res = qnDao.searchDescriptionContaining("qa", "qn");
		List<Questionnaire> res = qnDao.searchDescriptionContaining2("qa", "qn");
		res.forEach(item ->{
			System.out.println(item.getDescription());
		});
	}
	
	@Test
	public void joinTest() {
		List<QnQuVo> res = qnDao.selectJoinQnQu();
		res.forEach(item ->{
			System.out.printf("id: %d, title: %s, qu_id: %d \n", 
					item.getId(), item.getDescription(), item.getQuId());
		});
	}
	
	@Test
	public void selectFuzzyTest() {
		 QuizRes res = service.selectFuzzy("test", LocalDate.of(1971, 1, 1), LocalDate.of(2099, 1, 1));
		System.out.println(res.getQnQuVoList().size());
	}
	
	
}
