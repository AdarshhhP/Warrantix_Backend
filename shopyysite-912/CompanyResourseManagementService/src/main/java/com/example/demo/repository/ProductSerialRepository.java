package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.ProductSerial;

@Repository
public interface ProductSerialRepository extends JpaRepository<ProductSerial, Long> {

    @Query("SELECT ps FROM ProductSerial ps WHERE ps.Model_No = :modelNo AND ps.serialNo IN :serialNos")
    List<ProductSerial> findByModelNoAndSerialNos(@Param("modelNo") String modelNo, @Param("serialNos") List<String> serialNos);
    
    @Query("SELECT u FROM ProductSerial u WHERE u.product.prod_id = :productId And u.is_sold = :is_sold")
	Page<ProductSerial> getNotSoldSerials(
			@Param("is_sold") Integer is_sold,
			@Param("productId") Integer productId, 
			Pageable pageable);
    
    boolean existsBySerialNo(String serialNo);
}
