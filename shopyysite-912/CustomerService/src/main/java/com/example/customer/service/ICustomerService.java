package com.example.customer.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestParam;

import com.example.customer.model.ChangeApprovalStatus;
import com.example.customer.model.CompanyView;
import com.example.customer.model.CustomerDetails;
import com.example.demo.payload.CustomerRegPayload;
import com.example.demo.payload.PostResponse;
import com.example.demo.payload.RaiseWarrantyPayload;

public interface ICustomerService {
	public PostResponse registercustomer(@RequestBody CustomerRegPayload customerRegPayload);

	public Page<CustomerDetails> getWarrantyRequests(Integer customerId, String modelNo, Pageable pageable);   
	public Page<CustomerDetails> getWarrantyRequestsCompany(Integer customerId, String modelNo, Pageable pageable);   

	public Page<CompanyView> getWarrayRequestsByCustomers(Integer company_id, Integer status, String modelNo, LocalDate purchaseDate, 
			LocalDate warrantyPeriod, Integer customerId, LocalDate requestDateStart, LocalDate requestDateEnd , Pageable pageable );	
	
	
	public List<CompanyView> getRaisedWarrantyRequestsForCustomer(Integer userId, Integer status, String modelNo);
	 
	public PostResponse raiseWarrantyRequest(@RequestBody RaiseWarrantyPayload view);
	    
    CustomerDetails updateCustomer(Integer purchase_Id, CustomerDetails updatedCustomer);
    PostResponse deleteCustomer(Integer purchase_Id);
    public PostResponse WarrantyAction(@RequestParam Integer purchase_id , @RequestParam Integer status, @RequestParam(required = false) String rejection_remarks);
    public PostResponse deleteRaisedWarranty(@RequestParam Integer raised_Id);
    public PostResponse ChangeApprovalStatus(@RequestBody ChangeApprovalStatus changestatus);
	
}