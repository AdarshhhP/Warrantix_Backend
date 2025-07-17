package com.example.demo.response;

import java.util.List;

public class BulkUploadResponse {
	private Integer StatusCode;
	private String Message;
	private List<String> SuccessRecords;
	private List<String> FailedRecords;
	public Integer getStatusCode() {
		return StatusCode;
	}
	public void setStatusCode(Integer statusCode) {
		StatusCode = statusCode;
	}
	public String getMessage() {
		return Message;
	}
	public void setMessage(String message) {
		Message = message;
	}
	public List<String> getSuccessRecords() {
		return SuccessRecords;
	}
	public void setSuccessRecords(List<String> successRecords) {
		SuccessRecords = successRecords;
	}
	public List<String> getFailedRecords() {
		return FailedRecords;
	}
	public void setFailedRecords(List<String> failedRecords) {
		FailedRecords = failedRecords;
	}
}
