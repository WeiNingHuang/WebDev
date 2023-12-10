package com.example.questionnaire.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.questionnaire.service.ifs.QuizService;
import com.example.questionnaire.vo.QuestionDelVo;
import com.example.questionnaire.vo.QuestionRes;
import com.example.questionnaire.vo.QuestionnaireRes;
import com.example.questionnaire.vo.QuizReq;
import com.example.questionnaire.vo.QuizRes;
import com.example.questionnaire.vo.QuizSearchReq;

//屬於Rest風格的API
//命名通常依Service名稱來命名，故在此命為QuizController
//要import進來，故需在build gradle import => implementation 'org.springframework.boot:spring-boot-starter-web'
@RestController
@CrossOrigin // 跨接前端(前端fetch）
public class QuizController {

	@Autowired
	private QuizService service;

	// 從sevice複製方法來實作，外面傳遞資料為json格式，故需要用quiz來mapping，提供定值(URL)。

	@PostMapping(value = "api/quiz/create") // 提供外部介接 附帶HTTP請求方法Post，()內放入URL，透過URL即可存取，進行資源定位。
	public QuizRes create(@RequestBody QuizReq req) {
		return service.create(req);
	}

	@PostMapping(value = "api/quiz/update")
	public QuizRes update(@RequestBody QuizReq req) {
		return service.update(req);
	}
	
	@PostMapping(value = "api/quiz/deleteQuestionnaire")
	public QuizRes deleteQuestionnaire(@RequestBody List<Integer> qnIdList) {
		return service.deleteQuestionnaire(qnIdList);
	}
	
	@PostMapping(value = "api/quiz/deleteQuestion")
	public QuizRes deleteQuestion(@RequestBody QuestionDelVo delReq) {
		QuizRes delRes = service.deleteQuestion(delReq.getQnId(), delReq.getQuIdList());
		return new QuizRes(delRes.getRtnCode());
	}
	
	//fetch用 Get method的話，不能有request body，會報錯
	// mapping
	// QuizSearchReq的屬性名稱(對應在postman的key)=>但@JsonProperty可使其mapping外部資料(postman)在命名時的名稱，使使用者清楚，ex:start_date
	@GetMapping(value = "api/quiz/search")
	public QuizRes search(@RequestBody QuizSearchReq req) {
		String title = StringUtils.hasText(req.getTitle()) ? req.getTitle() : "";
		LocalDate startDate = req.getStartDate() != null ? req.getStartDate() : LocalDate.of(1971, 1, 1);
		LocalDate endDate = req.getEndDate() != null ? req.getEndDate() : LocalDate.of(2099, 12, 31);
		return service.search(title, startDate, endDate);
	}
	
	@GetMapping(value = "api/quiz/searchQuestionnaireList")
	public QuestionnaireRes searchQuestionnaireList(
			@RequestParam(value = "title", required = false, defaultValue = "") String title,
			@RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			@RequestParam(value = "isPublished", required = false)boolean isPublished) {
		title = StringUtils.hasText(title) ? title : "";
		startDate = startDate != null ? startDate : LocalDate.of(1971, 01, 01);
		endDate = endDate != null ? endDate : LocalDate.of(2099, 01, 01);
		return service.searchQuestionnaireList(title, startDate, endDate, isPublished);
	}
	
	@GetMapping(value = "api/quiz/searchQuestionList")
	public QuestionRes searchQuestionList(
			@RequestParam(value = "qnId", required = false, defaultValue="0") int qnId) {
		return service.searchQuestionList(qnId);
	}
	
}
