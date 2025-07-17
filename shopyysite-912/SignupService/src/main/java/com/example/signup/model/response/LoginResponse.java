package com.example.signup.model.response;


public class LoginResponse {

    private int statusCode;
    //private String message;  // ✅ use lowercase 'message'
    private String jwt;
    private Integer user_type;
    private Integer users_id;

    public Integer getUser_id() {
		return users_id;
	}


	public void setUser_id(Integer user_id) {
		this.users_id = user_id;
	}


	public Integer getUser_type() {
		return user_type;
	}


	public void setUser_type(Integer user_type) {
		this.user_type = user_type;
	}


	// Getters and Setters
    public int getStatusCode() {
        return statusCode;
    }

   
//    public String getMessage() {
//        return message;
//    }
//
//    public void setMessage(String message) {
//        this.message = message;
//    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

	public void setStatusCode(int statusCode2) {
        this.statusCode = statusCode2;
	}
}

