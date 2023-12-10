package com.example.questionnaire.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.questionnaire.entity.Question;

@Repository
public interface QuestionDao extends JpaRepository<Question, Integer>{
	
	//刪除問卷(其內的題目)用
	public void deleteAllByQnIdIn(List<Integer> qnIdList); //因為後面接的型態是List，沒有加In表示單個，兩者資料型態就會不匹配，故要加In以表示在在這之內(多個)進行deleteAll。

	//刪除題目用
	public void deleteAllByQnIdAndQuIdIn(int qnId, List<Integer> qnIdList); 
	
	//搜尋問卷其對應題目用
	public List<Question> findAllByQnIdIn(List<Integer> qnIdList);

	
}
