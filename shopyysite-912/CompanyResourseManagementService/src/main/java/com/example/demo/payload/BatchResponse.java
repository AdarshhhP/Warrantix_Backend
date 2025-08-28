package com.example.demo.payload;

import java.time.LocalDateTime;
import java.util.List;

public class BatchResponse {
	private Integer StatusCode;
	private String Message;
	private Integer batch_id;
    public Integer getBatch_id() {
		return batch_id;
	}

	public void setBatch_id(Integer batch_id) {
		this.batch_id = batch_id;
	}

	private String modelNo;
    private String batchNo;
    private LocalDateTime createdDate;
    public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	private List<String> serialNo;
    
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

    public String getModelNo() {
        return modelNo;
    }

    public void setModelNo(String modelNo) {
        this.modelNo = modelNo;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public List<String> getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(List<String> serialNo) {
        this.serialNo = serialNo;
    }
}
