package com.example.questionnaire.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name = "question")
@IdClass(value = QuestionId.class)
public class Question {
	
	@Id
	@Column(name = "id")
	private int quId;
	
	@Id
	@Column(name = "qn_id")
	private int qnId;
	
	@Column(name = "qn_title")
	private String qnTitle;
	
	@Column(name = "option_type")
	private String optionType;
	
	@Column(name = "is_necessary")
	private boolean necessary;
	
	@Column(name = "q_option")
	private String option;

	public Question() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Question(int quId,String qnTitle, String optionType, boolean necessary, String option) {
		super();
		this.quId = quId;
		this.qnTitle = qnTitle;
		this.optionType = optionType;
		this.necessary = necessary;
		this.option = option;
	}

	public Question(int quId, int qnId, String qnTitle, String optionType, boolean necessary, String option) {
		super();
		this.quId = quId;
		this.qnId = qnId;
		this.qnTitle = qnTitle;
		this.optionType = optionType;
		this.necessary = necessary;
		this.option = option;
	}

	

	public int getQuId() {
		return quId;
	}

	public void setQuId(int quId) {
		this.quId = quId;
	}

	public int getQnId() {
		return qnId;
	}

	public void setQnId(int qnId) {
		this.qnId = qnId;
	}

	public String getQnTitle() {
		return qnTitle;
	}

	public void setQnTitle(String qnTitle) {
		this.qnTitle = qnTitle;
	}

	public String getOptionType() {
		return optionType;
	}

	public void setOptionType(String optionType) {
		this.optionType = optionType;
	}

	public boolean isNecessary() {
		return necessary;
	}

	public void setNecessary(boolean necessary) {
		this.necessary = necessary;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	
}
