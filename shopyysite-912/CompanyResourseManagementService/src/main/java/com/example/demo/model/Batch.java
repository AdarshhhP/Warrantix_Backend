package com.example.demo.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "batch_table")
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer batch_id;

    private String modelNo;
    private String batch_no;
    private LocalDateTime createdDate;
	@OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<BatchProductMap> serialMappings = new ArrayList<>();

    // Getters and Setters
    public Integer getBatch_id() {
        return batch_id;
    }

    public void setBatch_id(Integer batch_id) {
        this.batch_id = batch_id;
    }

    public String getModel_no() {
        return modelNo;
    }

    public void setModel_no(String modelNo) {
        this.modelNo = modelNo;
    }

    public String getBatch_no() {
        return batch_no;
    }

    public void setBatch_no(String batch_no) {
        this.batch_no = batch_no;
    }

    public List<BatchProductMap> getSerialMappings() {
        return serialMappings;
    }

    public void setSerialMappings(List<BatchProductMap> serialMappings) {
        this.serialMappings = serialMappings;
    }
    public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}
}
