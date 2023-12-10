package com.example.questionnaire.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "questionnaire")
public class Questionnaire {

	@GeneratedValue(strategy = GenerationType.IDENTITY) //可即時返回AI值
	@Id
	@Column(name = "id")
	private int id;
	
	@Column(name = "title")
	private String title;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "is_published")
	private boolean published;  //布林值時，變數名稱不能加is! 在自動生成方法時，會自動生is，若變數名稱多打is之後再連接資料庫時會判別不到。
	
	@Column(name = "start_date")
	private LocalDate startDate;  //屬性日期要用LocalDate、LocalDate
	
	@Column(name = "end_date")
	private LocalDate endDate;

	public Questionnaire() {
		super();
		// TODO Auto-generated constructor stub
	}

	//因id使用AI自動生成，所以在建構方法不要生
	public Questionnaire(String title, String description, boolean published, LocalDate startDate, LocalDate endDate) {
		super();
		this.title = title;
		this.description = description;
		this.published = published;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	//test用
	public Questionnaire(int id, String title, String description, boolean published, LocalDate startDate,
			LocalDate endDate) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.published = published;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	//SQL語法示範用
	public Questionnaire(int id, String title, boolean published) {
		super();
		this.id = id;
		this.title = title;
		this.published = published;
	}

	public int getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isPublished() {
		return published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
}
