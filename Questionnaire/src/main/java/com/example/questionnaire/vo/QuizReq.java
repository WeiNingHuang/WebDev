package com.example.questionnaire.vo;

import java.util.List;

import com.example.questionnaire.entity.Question;
import com.example.questionnaire.entity.Questionnaire;

//從前端來的請求

public class QuizReq extends QuizVo{

	public QuizReq() {
		super();
	}

	public QuizReq(Questionnaire questionnaire, List<Question> questionList) {
		super(questionnaire, questionList);
	}

	//extends QuizVo，故以下皆不需要
	//使用時機：當一類別內還包含其他類別時。
	//因預設值為null，for迴圈或是執行其他操作會報錯。
	//方法一：在實際操作方法寫判斷式if(!=null)，或是方法二：設定預設值為空陣列、空class，只有裡面的屬性(id、title...)為null。
//	private Questionnaire questionnaire = new Questionnaire();
//	
//	private List<Question> questionList = new ArrayList<>();
	

}
