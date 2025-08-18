package com.example.demo.service;

import com.example.demo.model.Batch;
import com.example.demo.model.BatchProductMap;
import com.example.demo.payload.AddSerialRequest;
import com.example.demo.payload.BatchRequest;
import com.example.demo.payload.BatchResponse;
import com.example.demo.repository.BatchRepository;
import com.example.demo.repository.CompanyMgtRepository;
import com.example.demo.response.CreateBatchResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BatchService implements IBatchService {

	@Autowired
    private CompanyMgtRepository productRepository;
    @Autowired
    private BatchRepository batchRepository;

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
        String prefix = modelNo.substring(0, Math.min(3, modelNo.length())).toUpperCase();
        long count = batchRepository.countBymodelNoStartingWith(modelNo) + 1;
        String batchNo = prefix + "-" + String.format("%03d", count);
 
        Batch batch = new Batch();
        batch.setModel_no(modelNo);
        batch.setBatch_no(batchNo);
 
        request.getSerialNumbers().forEach(serial -> {
            BatchProductMap map = new BatchProductMap();
            map.setSerialNo(serial);
            map.setBatch(batch);
            batch.getSerialMappings().add(map);
        });
 
        Batch saved = batchRepository.save(batch);
 
        BatchResponse response = new BatchResponse();
        response.setModelNo(saved.getModel_no());
        response.setBatchNo(saved.getBatch_no());
        response.setSerialNo(saved.getSerialMappings()
                .stream()
                .map(BatchProductMap::getSerialNo)
                .collect(Collectors.toList()));
 
        return response;
    }

    
    @Override
    public List<BatchResponse> getAllBatches() {
        List<Batch> batches = batchRepository.findAll();

        return batches.stream().map(batch -> {
            BatchResponse response = new BatchResponse();
            response.setModelNo(batch.getModel_no());
            response.setBatchNo(batch.getBatch_no());
            response.setSerialNo(batch.getSerialMappings()
                .stream()
                .map(BatchProductMap::getSerialNo)
                .collect(Collectors.toList()));
            return response;
        }).collect(Collectors.toList());
    }
    
    @Override
    public Batch getSerialByBatchNo(@RequestParam String BatchNo) {
    	return batchRepository.getSerialByBatchNumber(BatchNo);
    }
    
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

        batchRepository.save(batch);

        response.setStatusCode(200);
        response.setMessage("Serial numbers added successfully");
        return response;
    }


}
