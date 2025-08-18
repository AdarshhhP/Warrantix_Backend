package com.example.demo.payload;

import java.util.List;

public class AddSerialRequest {
    private String batchNo;
    private List<String> serialNumbers;

    public String getBatchNo() {
        return batchNo;
    }
    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }
    public List<String> getSerialNumbers() {
        return serialNumbers;
    }
    public void setSerialNumbers(List<String> serialNumbers) {
        this.serialNumbers = serialNumbers;
    }
}
