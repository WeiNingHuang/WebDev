package com.example.questionnaire.vo;


import java.util.List;

import com.example.questionnaire.constants.RtnCode;

public class QuizRes {
	
	//不接受此寫法，因為當這樣寫時，若五張問卷各四題，搜尋時會有qnLIst5筆、quList20筆，當點進去問卷時，就要從quList中找對應的問卷qnId，功在前端。
//	private List<Questionnaire> questionnaireList;
//	private List<Question> questionList;
	//多個組合：一張問卷多個題目的組合，故用List包起來
	private List<QuizVo> quizVoList;
	
	private List<QnQuVo> qnQuVoList;

	private RtnCode rtnCode;
	

	public QuizRes() {
		super();
	}

	public QuizRes(RtnCode rtnCode) {
		super();
		this.rtnCode = rtnCode;
	}
	
	public QuizRes(List<QuizVo> quizVoList, RtnCode rtnCode) {
		super();
		this.quizVoList = quizVoList;
		this.rtnCode = rtnCode;
	}
	

	public QuizRes(List<QuizVo> quizVoList, List<QnQuVo> qnQuVoList, RtnCode rtnCode) {
		super();
		this.quizVoList = quizVoList;
		this.qnQuVoList = qnQuVoList;
		this.rtnCode = rtnCode;
	}

	public RtnCode getRtnCode() {
		return rtnCode;
	}

	public void setRtnCode(RtnCode rtnCode) {
		this.rtnCode = rtnCode;
	}

	public List<QuizVo> getQuizVoList() {
		return quizVoList;
	}

	public void setQuizVoList(List<QuizVo> quizVoList) {
		this.quizVoList = quizVoList;
	}

	public List<QnQuVo> getQnQuVoList() {
		return qnQuVoList;
	}

	public void setQnQuVoList(List<QnQuVo> qnQuVoList) {
		this.qnQuVoList = qnQuVoList;
	}

	
	
}
