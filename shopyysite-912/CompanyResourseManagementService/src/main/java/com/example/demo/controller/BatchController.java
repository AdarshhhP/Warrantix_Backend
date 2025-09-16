package com.example.demo.controller;

import com.example.demo.model.Batch;
import com.example.demo.payload.AddSerialRequest;
import com.example.demo.payload.BatchRequest;
import com.example.demo.payload.BatchResponse;
import com.example.demo.payload.RemoveSerialRequest;
import com.example.demo.response.CreateBatchResponse;
import com.example.demo.service.IBatchService;
import org.springframework.data.domain.Page;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/batch")
public class BatchController {

    @Autowired
    private IBatchService batchService;
    
    // create new batch
    @PostMapping("/create")
    public ResponseEntity<BatchResponse> createBatch(@RequestBody BatchRequest request) {
        BatchResponse response = batchService.createBatch(request);
        return ResponseEntity.ok(response);
    }
    
    // Get a list of all batches
    @GetMapping("/list")
    public Page<BatchResponse> listBatches(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
	    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "batch_id"));


        return batchService.getAllBatches(pageable);
    }
    
    @GetMapping("/getModelByBatchNo")
    public Batch getModelBySerialNo(@RequestParam String BatchNo) {
    	return batchService.getModelBySerialNo(BatchNo);
    }
    
    // Get a batch by batch number
    @GetMapping("/getSerialByModelNo")
    public Batch getSerialByBatchNo(@RequestParam String BatchNo) {
    	return batchService.getSerialByBatchNo(BatchNo);
    }
    
    // Add serial numbers to an existing batch.
    @PostMapping("/add-serials")
    public ResponseEntity<CreateBatchResponse> addSerials(@RequestBody AddSerialRequest request) {
        return ResponseEntity.ok(batchService.addSerialsToBatch(request));
    }
    
    // GET API to fetch batch details by batchId
    @GetMapping("/batchId")
    public ResponseEntity<Batch> getBatchById(@RequestParam Integer batchId) {
        return batchService.getBatchById(batchId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Post api for removing the serial number
    @PostMapping("/remove-serial")
    public ResponseEntity<CreateBatchResponse> removeSerial(@RequestBody RemoveSerialRequest request) {
        CreateBatchResponse response = batchService.removeSerialFromBatch(request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

}
