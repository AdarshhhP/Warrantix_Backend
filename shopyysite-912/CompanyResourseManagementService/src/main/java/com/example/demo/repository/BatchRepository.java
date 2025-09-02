package com.example.demo.repository;
 
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;
 
import com.example.demo.model.Batch;
import com.example.demo.payload.BatchResponse;
 
public interface BatchRepository extends JpaRepository<Batch, Integer>{
	// Counts the number of Batch records where the model number starts with the given prefix.
	long countBymodelNoStartingWith(String modelNo);
	
	// Retrieves a Batch entity based on the given batch number.
	@Query("Select u from Batch u where u.batch_no=:BatchNo")
	Batch getSerialByBatchNumber(@RequestParam String BatchNo);
	
	@Query("Select b from Batch b")
	Page<Batch>getAllBatches(Pageable pagaeble);
} 
