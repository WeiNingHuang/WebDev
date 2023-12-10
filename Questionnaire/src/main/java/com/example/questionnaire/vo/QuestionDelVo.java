package com.example.questionnaire.vo;

import java.util.List;

public class QuestionDelVo {

	private int qnId;
	
	List<Integer> quIdList;

	public QuestionDelVo() {
		super();
	}

	public QuestionDelVo(int qnId, List<Integer> quIdList) {
		super();
		this.qnId = qnId;
		this.quIdList = quIdList;
	}

	public int getQnId() {
		return qnId;
	}

	public void setQnId(int qnId) {
		this.qnId = qnId;
	}

	public List<Integer> getQuIdList() {
		return quIdList;
	}

	public void setQuIdList(List<Integer> quIdList) {
		this.quIdList = quIdList;
	}
	
	
}
