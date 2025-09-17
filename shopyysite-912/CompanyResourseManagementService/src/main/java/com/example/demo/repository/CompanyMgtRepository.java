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

import com.example.demo.model.ProductDetails;
import com.example.demo.model.ProductSerial;

@Repository
public interface CompanyMgtRepository extends JpaRepository<ProductDetails, Integer> {
	
   //Get paginated product list with optional filters
	@Query("SELECT p FROM ProductDetails p\r\n"+ "WHERE p.Company_id = :companyId\r\n"
	        + "AND (:holderStatus IS NULL OR p.HolderStatus = :holderStatus)\r\n"
			+ "AND (:productCategory IS NULL OR p.Product_category = :productCategory)\r\n"
			+ "AND (:manDate IS NULL OR p.Man_date = :manDate)\r\n"
		    + "AND (:modelNo IS NULL OR p.Model_no LIKE CONCAT('%', :modelNo, '%'))\r\n"
			+ "AND (:productName IS NULL OR p.Product_name LIKE CONCAT('%', :productName, '%'))"
			)
	 Page<ProductDetails> getProducts(@Param("companyId") Integer companyId,@Param("holderStatus") Integer holderStatus,
		        @Param("productCategory") String productCategory,@Param("modelNo") String modelNo, @Param("productName") String productName,@Param("manDate") LocalDate manDate,Pageable pageable
		    );
	
	//Get product details by model number.
	@Query("Select u from ProductDetails u where u.Model_no=:Model_no")
	ProductDetails getProductDetailsByModelNo(@RequestParam String Model_no);

	
	//Get multiple products based on a list of model numbers.
	@Query("SELECT u FROM ProductDetails u WHERE u.Model_no IN :modelNos")
	List<ProductDetails> getProductsByModelNos(@Param("modelNos") List<String> modelNos);
	
	//Update holder status of a product using model number.
	@Modifying
	@Query("Update ProductDetails p set p.HolderStatus=:status where p.Model_no=:Model_no")
	Integer ChangeholderStatus(@RequestParam String Model_no,@RequestParam Integer status);
	
	//Check product eligibility based on model number and holder status.
	@Query("SELECT u FROM ProductDetails u WHERE u.Model_no = :Model_no AND u.HolderStatus = :checkvalue-1")
	List<ProductDetails> CheckEligibility(
	    @Param("Model_no") String modelNo,
	    @Param("checkvalue") int checkvalue);
	
    //  Checks whether a ProductDetails record exists for a given model number.
	@Query("SELECT COUNT(p) > 0 FROM ProductDetails p WHERE p.Model_no = :modelNo")
	boolean existsByModelNo(@Param("modelNo") String modelNo);
	
	// Retrieves a ProductDetails entity by its product ID.
    @Query("SELECT u FROM ProductDetails u WHERE u.prod_id IN :ProductId")
    ProductDetails getProductDetailsByProductId(@RequestParam Integer ProductId);
}


