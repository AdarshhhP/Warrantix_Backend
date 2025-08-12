package com.example.demo.controller;

import com.example.demo.model.Batch;
import com.example.demo.payload.BatchRequest;
import com.example.demo.payload.BatchResponse;
import com.example.demo.service.IBatchService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/batch")
public class BatchController {

    @Autowired
    private IBatchService batchService;

    @PostMapping("/create")
    public ResponseEntity<BatchResponse> createBatch(@RequestBody BatchRequest request) {
        BatchResponse response = batchService.createBatch(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/list")
    public ResponseEntity<List<BatchResponse>> listBatches() {
        List<BatchResponse> responses = batchService.getAllBatches();
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/getSerialByModelNo")
    public Batch getSerialByBatchNo(@RequestParam String BatchNo) {
    	return batchService.getSerialByBatchNo(BatchNo);
    }

}
