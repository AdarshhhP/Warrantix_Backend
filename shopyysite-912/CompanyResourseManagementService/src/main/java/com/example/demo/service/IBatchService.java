package com.example.demo.service;

import com.example.demo.payload.BatchRequest;
import com.example.demo.payload.BatchResponse;

public interface IBatchService {
    BatchResponse createBatch(BatchRequest request);
}
