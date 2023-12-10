package com.example.questionnaire.service.ifs;

import java.time.LocalDate;
import java.util.List;

import com.example.questionnaire.vo.QuestionRes;
import com.example.questionnaire.vo.QuestionnaireRes;
import com.example.questionnaire.vo.QuizReq;
import com.example.questionnaire.vo.QuizRes;

public interface QuizService {

	public QuizRes create(QuizReq req);
	
	public QuizRes update(QuizReq req);
	
	public QuizRes deleteQuestionnaire(List<Integer> qnIdList); //刪除多張問卷
	
	public QuizRes deleteQuestion(int qnId, List<Integer> quIdList); //刪除同張問卷內的多個問題
	
	public QuizRes search(String title, LocalDate startDate, LocalDate endDate); //模糊搜尋
	
	public QuestionnaireRes searchQuestionnaireList(String title, LocalDate startDate, LocalDate endDate, boolean isPublished);  //模糊搜尋(分開版-問卷）
	
	public QuestionRes searchQuestionList(int qnId);  //模糊搜尋(分開版-題目）
	
	public QuizRes selectFuzzy(String title, LocalDate startDate, LocalDate endDate);
}
