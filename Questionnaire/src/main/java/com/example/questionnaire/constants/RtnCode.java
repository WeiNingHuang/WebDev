package com.example.questionnaire.constants;

//Enum列舉
public enum RtnCode {
	
	//http status code
	//1.需建立constructor，否則SUCCESSFUL會報錯
	SUCCESSFUL(200, "Successful!"), //
	QUESTIONNAIRE_PARAM_ERROR(400, "Questionnaire Param Error!"), //
	QUESTION_PARAM_ERROR(400, "Question Param Error!"), //
	QUESTIONNAIRE_ID_PARAM_ERROR(400, "Questionnaire Id Param Error!"), //
	QUESTIONNAIRE_ID_NOT_FOUND(404, "Questionnaire Id Not Found!"), //
	UPDATE_ERROR(400, "Update Error!"), //
	;  

	private int code;
	
	private String message;

	//2.右鍵Source -> Generate constructor using Fields (* superClass預設建構方法不能用在Enum)
	private RtnCode(int code, String message) {
		this.code = code;
		this.message = message;
	}

	//3.右鍵Source -> Generate Getter and Setters (只會用到getter)
	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
	
	
}
