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

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public Page<BatchResponse> getAllBatches(Pageable pageable) {
        Page<Batch> batches = batchRepository.getAllBatches(pageable);

        // Map Batch → BatchResponse while preserving pagination
        return batches.map(batch -> {
            BatchResponse response = new BatchResponse();
            response.setBatch_id(batch.getBatch_id());
            response.setModelNo(batch.getModel_no());
            response.setBatchNo(batch.getBatch_no());
            response.setCreatedDate(batch.getCreatedDate());
            response.setSerialNo(
                batch.getSerialMappings()
                    .stream()
                    .map(BatchProductMap::getSerialNo)
                    .collect(Collectors.toList())
            );
            return response;
        });
    }

    // Retrieves a batch and its serial mappings by batch number.
    @Override
    public Batch getSerialByBatchNo(@RequestParam String BatchNo) {
    	return batchRepository.getSerialByBatchNumber(BatchNo);
    }
    
    // Adds new serial numbers to an existing batch
    @Override
    @Transactional
    public CreateBatchResponse addSerialsToBatch(AddSerialRequest request) {
        CreateBatchResponse response = new CreateBatchResponse();

        // Find batch
        Batch batch = batchRepository.getSerialByBatchNumber(request.getBatchNo());
        if (batch == null) {
            response.setStatusCode(404);
            response.setMessage("Batch not found");
            return response;
        }

        // Ensure serialMappings is initialized
        if (batch.getSerialMappings() == null) {
            batch.setSerialMappings(new ArrayList<>());
        }

        // Add new serials (skip duplicates)
        for (String serial : request.getSerialNumbers()) {
            boolean exists = batch.getSerialMappings().stream()
                    .anyMatch(m -> m.getSerialNo().equals(serial));

            if (!exists) {
                BatchProductMap map = new BatchProductMap();
                map.setSerialNo(serial);
                map.setBatch(batch);
                batch.getSerialMappings().add(map);
            }
        }

        // Save batch (cascade will handle BatchProductMap)
        batchRepository.save(batch);

        response.setStatusCode(200);
        response.setMessage("Serial numbers added successfully");
//        response.setBatchNo(batch.getBatch_no());
//        response.setModelNo(batch.getModel_no());
//        response.setSerialNo(
//                batch.getSerialMappings().stream()
//                        .map(BatchProductMap::getSerialNo)
//                        .collect(Collectors.toList())
//        );

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
    
    public Batch getModelBySerialNo(@RequestParam String BatchNo) {
    	return batchRepository.getModelBySerialNo(BatchNo);
    }

}
