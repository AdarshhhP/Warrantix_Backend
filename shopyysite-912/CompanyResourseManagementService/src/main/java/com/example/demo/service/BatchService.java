package com.example.demo.service;

import com.example.demo.model.Batch;
import com.example.demo.model.BatchProductMap;
import com.example.demo.payload.AddSerialRequest;
import com.example.demo.payload.BatchRequest;
import com.example.demo.payload.BatchResponse;
import com.example.demo.payload.RemoveSerialRequest;
import com.example.demo.repository.BatchRepository;
import com.example.demo.repository.CompanyMgtRepository;
import com.example.demo.response.CreateBatchResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BatchService implements IBatchService {

	@Autowired
    private CompanyMgtRepository productRepository;
    @Autowired
    private BatchRepository batchRepository;

    // Creates a new batch for a given model number.
    @Override
    public BatchResponse createBatch(BatchRequest request) {
        String modelNo = request.getModelNo();
     // ✅ Check if model number exists
        BatchResponse br=new BatchResponse();
        if (!productRepository.existsByModelNo(modelNo)) {
            br.setStatusCode(404);
            br.setMessage("Model No Doesnt Exist");
            return br;
        }
        // Generate batch number: Prefix (first 3 chars of modelNo) + counter
        String prefix = modelNo.substring(0, Math.min(3, modelNo.length())).toUpperCase();
        long count = batchRepository.countBymodelNoStartingWith(modelNo) + 1;
        String batchNo = prefix + "-" + String.format("%03d", count);
 
        // Create a new Batch entity
        Batch batch = new Batch();
        batch.setModel_no(modelNo);
        batch.setBatch_no(batchNo);
        batch.setCreatedDate(LocalDateTime.now());
        
        // Map all serial numbers to BatchProductMap
        request.getSerialNumbers().forEach(serial -> {
            BatchProductMap map = new BatchProductMap();
            map.setSerialNo(serial);
            map.setBatch(batch);
            batch.getSerialMappings().add(map);
        });
        // Save batch into DB
        Batch saved = batchRepository.save(batch);
        // Prepare response
        BatchResponse response = new BatchResponse();
        response.setModelNo(saved.getModel_no());
        response.setBatchNo(saved.getBatch_no());
        response.setCreatedDate(saved.getCreatedDate());
        response.setSerialNo(saved.getSerialMappings()
                .stream()
                .map(BatchProductMap::getSerialNo)
                .collect(Collectors.toList()));
 
        return response;
    }

    // Retrieves all batches with their serial numbers.
    @Override
    public List<BatchResponse> getAllBatches() {
        List<Batch> batches = batchRepository.findAll();
        // Map Batch entities → BatchResponse DTOs
        return batches.stream().map(batch -> {
            BatchResponse response = new BatchResponse();
            response.setBatch_id(batch.getBatch_id());
            response.setModelNo(batch.getModel_no());
            response.setBatchNo(batch.getBatch_no());
            response.setCreatedDate(batch.getCreatedDate());
            response.setSerialNo(batch.getSerialMappings()
                .stream()
                .map(BatchProductMap::getSerialNo)
                .collect(Collectors.toList()));
            return response;
        }).collect(Collectors.toList());
    }
    
    // Retrieves a batch and its serial mappings by batch number.
    @Override
    public Batch getSerialByBatchNo(@RequestParam String BatchNo) {
    	return batchRepository.getSerialByBatchNumber(BatchNo);
    }
    
    // Adds new serial numbers to an existing batch
    @Override
    public CreateBatchResponse addSerialsToBatch(AddSerialRequest request) {
        CreateBatchResponse response = new CreateBatchResponse();

        // Find the batch by batch number
        Batch batch = batchRepository.getSerialByBatchNumber(request.getBatchNo());
        if (batch == null) {
            response.setStatusCode(404);
            response.setMessage("Batch not found");
            return response;
        }

        // Add new serial numbers
        for (String serial : request.getSerialNumbers()) {
            BatchProductMap map = new BatchProductMap();
            map.setSerialNo(serial);
            map.setBatch(batch);
            batch.getSerialMappings().add(map);
        }
        // Save updated batch
        batchRepository.save(batch);

        response.setStatusCode(200);
        response.setMessage("Serial numbers added successfully");
        return response;
    }

    // Get Api for getting the list by batch_id
    @Override
    public Optional<Batch> getBatchById(@RequestParam Integer batchId) {
        return batchRepository.findById(batchId);
    }

    //
    @Override
    public CreateBatchResponse removeSerialFromBatch(RemoveSerialRequest request) {
        CreateBatchResponse response = new CreateBatchResponse();

        // Find the batch
        Batch batch = batchRepository.getSerialByBatchNumber(request.getBatchNo());
        if (batch == null) {
            response.setStatusCode(404);
            response.setMessage("Batch not found");
            return response;
        }

        // Find the serial mapping
        BatchProductMap serialMap = batch.getSerialMappings().stream()
                .filter(m -> m.getSerialNo().equals(request.getSerialNumber()))
                .findFirst()
                .orElse(null);

        if (serialMap == null) {
            response.setStatusCode(404);
            response.setMessage("Serial number not found in this batch");
            return response;
        }

        // Remove mapping
        batch.getSerialMappings().remove(serialMap);

        // If cascade is not enabled, explicitly delete from repository:
        // batchProductMapRepository.delete(serialMap);

        batchRepository.save(batch);

        response.setStatusCode(200);
        response.setMessage("Serial number removed successfully");
        return response;
    }

}
