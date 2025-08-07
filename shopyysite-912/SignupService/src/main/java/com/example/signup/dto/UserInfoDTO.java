package com.example.signup.dto;

public class UserInfoDTO {
	private Integer user_id;
	public Integer getUserId() {
		return user_id;
	}

	public void setUserId(Integer userId) {
		this.user_id = userId;
	}

	private String userName;
	private String email;
	
	public UserInfoDTO(Integer user_id,String userName, String email) {
		this.user_id = user_id;
		this.userName = userName;
		this.email = email;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
}
