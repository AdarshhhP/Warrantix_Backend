package com.example.customer.respository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.customer.model.CompanyView;
import com.example.customer.model.CustomerDetails;

public interface CustomerRepository extends JpaRepository<CustomerDetails, Integer> {

	@Query("SELECT c FROM CustomerDetails c WHERE c.customerId = :customerId AND c.isDeleted = 0")
	List<CustomerDetails> getWarrantyRequests(@Param("customerId") Integer customerId);


    @Query("SELECT c FROM CustomerDetails c WHERE c.customerId = :customerId AND c.isDeleted = 0")
    List<CustomerDetails> findByCustomerIdAndNotDeleted(@Param("customerId") Integer customerId);
    
    @Query("SELECT COUNT(c) > 0 FROM CustomerDetails c WHERE c.model_no = :model_no")
    boolean existsByModelNo(@Param("model_no") String model_no);
    
    @Modifying
    @Query("UPDATE CustomerDetails c set c.isDeleted=1 where c.purchase_Id=:purchase_Id")
    Integer deleteCustomer(Integer purchase_Id);
    
    @Query("SELECT c FROM CustomerDetails c WHERE c.isDeleted = 0 AND " +
    	       "(:modelNo IS NULL OR c.model_no = :modelNo) AND " +
    	       "(:customerId IS NULL OR c.customerId = :customerId)")
    	List<CustomerDetails> findFilteredCustomerDetails(
    	    @Param("modelNo") String modelNo,
    	    @Param("customerId") Integer customerId
    	);




}
