package com.example.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.ProductSerial;

@Repository
public interface ProductSerialRepository extends JpaRepository<ProductSerial, Long> {

    @Query("SELECT u FROM ProductSerial u WHERE u.product.prod_id = :productId And u.is_sold = :is_sold")
	Page<ProductSerial> getNotSoldSerials(
			@Param("is_sold") Integer is_sold,
			@Param("productId") Integer productId, 
			Pageable pageable);

}
