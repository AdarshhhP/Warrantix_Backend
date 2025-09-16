package com.example.demo.response;

import java.time.LocalDateTime;
import java.util.List;

public class SerialResponseByBatchNo {

    private Integer batch_id;
    private String batch_no;
    private LocalDateTime createdDate;
    private List<SerialMapping> serialMappings;
    private String model_no;

    // Getters and Setters
    public Integer getBatch_id() {
        return batch_id;
    }

    public void setBatch_id(Integer batch_id) {
        this.batch_id = batch_id;
    }

    public String getBatch_no() {
        return batch_no;
    }

    public void setBatch_no(String batch_no) {
        this.batch_no = batch_no;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public List<SerialMapping> getSerialMappings() {
        return serialMappings;
    }

    public void setSerialMappings(List<SerialMapping> serialMappings) {
        this.serialMappings = serialMappings;
    }

    public String getModel_no() {
        return model_no;
    }

    public void setModel_no(String model_no) {
        this.model_no = model_no;
    }

    // Inner class for SerialMappings
    public static class SerialMapping {
        private Integer id;
        private String serialNo;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getSerialNo() {
            return serialNo;
        }

        public void setSerialNo(String serialNo) {
            this.serialNo = serialNo;
        }
    }
}
