package com.example.signup.dto;

import java.util.List;

public class UserListResponse {

    private String message;
    private int statusCode;
    private List<UserInfoUserType> userinfo;

    // ✅ Add these fields
    private int totalPages;
    private long totalElements;
    private int currentPage;

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public List<UserInfoUserType> getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(List<UserInfoUserType> userinfo) {
        this.userinfo = userinfo;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}
