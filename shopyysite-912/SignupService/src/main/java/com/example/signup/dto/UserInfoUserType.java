package com.example.signup.dto;

public class UserInfoUserType {
	private String userName;
	private String email;
	private Integer userType;
	private Integer userId;
	private Integer active_status;
	public Integer getUserId() {
		return userId;
	}

	public Integer getActive_status() {
		return active_status;
	}

	public void setActive_status(Integer active_status) {
		this.active_status = active_status;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public UserInfoUserType(String userName, String email,Integer userType,Integer userId,Integer active_status) {
		this.userName = userName;
		this.email = email;
		this.userType=userType;
		this.userId=userId;
		this.active_status=active_status;
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
