package com.example.signup.model.response;


public class LoginResponse {

    private int statusCode;
    //private String message;  // ✅ use lowercase 'message'
    private String jwt;
    private Integer user_type;
    private Integer users_id;
    private String user_name;
    private String email;
    public Integer getUsers_id() {
		return users_id;
	}

	public void setUsers_id(Integer users_id) {
		this.users_id = users_id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

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

