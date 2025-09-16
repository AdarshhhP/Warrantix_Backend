package com.example.demo.service;

import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.demo.model.Batch;
import com.example.demo.payload.AddSerialRequest;
import com.example.demo.payload.BatchRequest;
import com.example.demo.payload.BatchResponse;
import com.example.demo.payload.RemoveSerialRequest;
import com.example.demo.response.CreateBatchResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IBatchService {
    BatchResponse createBatch(BatchRequest request);
    Page<BatchResponse> getAllBatches(Pageable pageable);
    public Batch getSerialByBatchNo(@RequestParam String BatchNo);
    CreateBatchResponse addSerialsToBatch(AddSerialRequest request);
    public Optional<Batch> getBatchById(@RequestParam Integer batchId);
    public CreateBatchResponse removeSerialFromBatch(RemoveSerialRequest request);
    public Batch getModelBySerialNo(@RequestParam String BatchNo);
}
