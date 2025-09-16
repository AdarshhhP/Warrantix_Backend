package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.ProductSerial;

@Repository
public interface ProductSerialRepository extends JpaRepository<ProductSerial, Long> {
    // Custom query to fetch all ProductSerials by a given model number and a list of serial numbers.
    @Query("SELECT ps FROM ProductSerial ps WHERE ps.Model_No = :modelNo AND ps.serialNo IN :serialNos")
    List<ProductSerial> findByModelNoAndSerialNos(@Param("modelNo") String modelNo, @Param("serialNos") List<String> serialNos);
    // Fetches paginated ProductSerials
    @Query("SELECT u FROM ProductSerial u WHERE u.product.prod_id = :productId And u.is_sold = :is_sold")
	Page<ProductSerial> getNotSoldSerials(
			@Param("is_sold") Integer is_sold,
			@Param("productId") Integer productId, 
			Pageable pageable);
    
 // With search filter
    @Query("SELECT u FROM ProductSerial u " +
           "WHERE u.product.prod_id = :productId " +
           "AND u.is_sold = :is_sold " +
           "AND (:serialNo IS NULL OR u.serialNo LIKE %:serialNo%)")
    Page<ProductSerial> searchNotSoldSerials(
            @Param("is_sold") Integer is_sold,
            @Param("serialNo") String serialNo,
            @Param("productId") Integer productId,
            Pageable pageable);
    
    @Query("SELECT ps FROM ProductSerial ps " +
    	       "JOIN BatchProductMap bpm ON ps.serialNo = bpm.serialNo " +
    	       "JOIN bpm.batch b " +
    	       "WHERE ps.product.prod_id = :productId " +
    	       "AND ps.is_sold = :is_sold")
    	Page<ProductSerial> getBatchedSerials(
    	        @Param("is_sold") Integer is_sold,
    	        @Param("productId") Integer productId,
    	        Pageable pageable);

    
    boolean existsBySerialNo(String serialNo);
    
    @Modifying(clearAutomatically = true, flushAutomatically = true) // mark this as an update query
    @Transactional // ensure transaction boundary
    @Query("UPDATE ProductSerial p SET p.is_sold = :status WHERE p.serialNo = :serialNo")
    int updateSerialStatus(@Param("serialNo") String serialNo, @Param("status") Integer status);
}
