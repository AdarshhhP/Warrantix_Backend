package com.example.demo.service;

import java.util.List;

import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Batch;
import com.example.demo.payload.AddSerialRequest;
import com.example.demo.payload.BatchRequest;
import com.example.demo.payload.BatchResponse;
import com.example.demo.response.CreateBatchResponse;

public interface IBatchService {
    BatchResponse createBatch(BatchRequest request);
    List<BatchResponse> getAllBatches();
    public Batch getSerialByBatchNo(@RequestParam String BatchNo);
    CreateBatchResponse addSerialsToBatch(AddSerialRequest request);

}
