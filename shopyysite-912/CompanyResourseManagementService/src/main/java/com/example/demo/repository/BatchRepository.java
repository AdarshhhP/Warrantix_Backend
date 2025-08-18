package com.example.demo.repository;
 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;
 
import com.example.demo.model.Batch;
import com.example.demo.payload.BatchResponse;
 
public interface BatchRepository extends JpaRepository<Batch, Integer>{
	long countBymodelNoStartingWith(String modelNo);
	
	@Query("Select u from Batch u where u.batch_no=:BatchNo")
	Batch getSerialByBatchNumber(@RequestParam String BatchNo);
}

