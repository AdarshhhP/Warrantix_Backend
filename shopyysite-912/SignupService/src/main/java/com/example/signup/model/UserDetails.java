package com.example.signup.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name="user_table")
public class UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer user_id;
    
    @NotBlank(message="Username required")
    private String userName;
    
    @NotBlank(message="Email required")
    @Email(message="Provid a proper email id")
    private String email;
    
    @NotBlank(message="Password required")
    private String password;
    
    private Integer userType;
    
	private Integer is_deleted = 0;


    public Integer getUser_id() {
		return user_id;
	}
	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}
	public Integer getIs_deleted() {
		return is_deleted;
	}
	public void setIs_deleted(Integer is_deleted) {
		this.is_deleted = is_deleted;
	}
	// Getters and setters
    public Integer getUserId() {
        return user_id;
    }
    public void setUserId(Integer userId) {
        this.user_id = userId;
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
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Integer getUserType() {
        return userType;
    }
    public void setUserType(Integer userType) {
        this.userType = userType;
    }
}
