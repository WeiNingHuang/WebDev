package com.example.questionnaire.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class QuestionId implements Serializable{

	private int quId;
	
	private int qnId;

	public QuestionId() {
		super();
		// TODO Auto-generated constructor stub
	}

	public QuestionId(int quId, int qnId) {
		super();
		this.quId = quId;
		this.qnId = qnId;
	}

	
	//基本上用不到，但習慣生成一下
	
	public int getQnId() {
		return qnId;
	}

	public int getQuId() {
		return quId;
	}

	public void setQuId(int quId) {
		this.quId = quId;
	}

	public void setQnId(int qnId) {
		this.qnId = qnId;
	}
	
	
}
