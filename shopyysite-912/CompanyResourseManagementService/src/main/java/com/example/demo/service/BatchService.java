package com.example.demo.service;

import com.example.demo.model.Batch;
import com.example.demo.model.BatchProductMap;
import com.example.demo.payload.BatchRequest;
import com.example.demo.payload.BatchResponse;
import com.example.demo.repository.BatchRepository;
import com.example.demo.repository.CompanyMgtRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        if (!productRepository.existsByModelNo(modelNo)) {
            throw new IllegalArgumentException("Model number " + modelNo + " does not exist.");
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
}
