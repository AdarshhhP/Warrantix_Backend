package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Batch;

public interface BatchRepository extends JpaRepository<Batch, Integer>{
	long countBymodelNoStartingWith(String modelNo);
}
