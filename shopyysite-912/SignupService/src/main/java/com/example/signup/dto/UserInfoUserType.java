package com.example.signup.dto;

public class UserInfoUserType {
	private String userName;
	private String email;
	private Integer userType;
	
	public UserInfoUserType(String userName, String email,Integer userType) {
		this.userName = userName;
		this.email = email;
		this.userType=userType;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
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
