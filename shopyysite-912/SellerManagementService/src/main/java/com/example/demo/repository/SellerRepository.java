package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.InventoryItem;
import com.example.demo.response.PostResponse;

@Repository
public interface SellerRepository extends JpaRepository<InventoryItem, Integer>  {
	
	@Modifying
	@Query("Update InventoryItem u set u.is_deleted=1 where u.purchase_id=:purchase_id")
	int DeleteInventory(@Param("purchase_id") Integer purchase_id);
	
	@Query("SELECT i FROM InventoryItem i " +
		       "WHERE i.seller_id = :sellerId AND i.is_deleted = 0 " +
		       "AND (:categoryId IS NULL OR i.Category_id = :categoryId) " +
		       "AND ((:modelNo IS NULL OR :modelNo = '') OR i.Model_no = :modelNo) " +
		       "AND (:warranty IS NULL OR i.warranty = :warranty) " +
		       "AND (:purchaseDate IS NULL OR i.purchase_date = :purchaseDate)")
		Page<InventoryItem> findByFilters(
		    @Param("sellerId") Integer sellerId,
		    @Param("categoryId") Integer categoryId,
		    @Param("modelNo") String modelNo,
		    @Param("warranty") Integer warranty,
		    @Param("purchaseDate") LocalDate purchaseDate,
		    Pageable pageable
		);



}
