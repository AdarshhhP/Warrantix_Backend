package com.example.customer.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.customer.model.CompanyView;
import com.example.customer.model.CustomerDetails;
import com.example.customer.respository.CompanyViewRepository;
import com.example.customer.respository.CustomerRepository;
import com.example.demo.payload.CustomerRegPayload;
import com.example.demo.payload.PostResponse;
import com.example.demo.payload.RaiseWarrantyPayload;

import jakarta.transaction.Transactional;

@Service
public class CustomerService implements ICustomerService  {
	@Autowired
	private CustomerRepository repository;
	@Autowired
	private CompanyViewRepository companyviewrepository;
	
	// Register a customer for warranty
	@Transactional
	@Override
	public PostResponse registercustomer(@RequestBody CustomerRegPayload customerRegPayload) {
	    PostResponse response = new PostResponse();

	    // Step 1: Check if model_no already exists
	    boolean modelExists = repository.existsByModelNo(customerRegPayload.getModel_no());

//	    if (modelExists) {
//	        response.setStatusCode(409); // Conflict
//	        response.setMessage("Model number doesnt exists. Cannot register again.");
//	        return response;
//	    }

	    // Step 2: Save if model_no is unique
	    CustomerDetails cd = new CustomerDetails();
	    cd.setModel_no(customerRegPayload.getModel_no());
	    cd.setPurchase_date(customerRegPayload.getPurchase_date());
	    cd.setCustomerId(customerRegPayload.getCustomerId());

	    CustomerDetails saved = repository.save(cd);

	    if (saved.getCustomerId() != null) {
	        response.setStatusCode(200);
	        response.setMessage("Successfully registered.");
	    } else {
	        response.setStatusCode(500);
	        response.setMessage("Registration failed.");
	    }

	    return response;
	}

    // Get filtered warranty requests for the company
	@Override
	public Page<CompanyView> getWarrayRequestsByCustomers(Integer company_id, Integer status, String modelNo, LocalDate purchaseDate, 
			LocalDate warrantyPeriod, Integer customerId, LocalDate requestDateStart, LocalDate requestDateEnd , Pageable pageable ) {
	   
	    return companyviewrepository.findFilteredCompanyViews(company_id,status,modelNo,purchaseDate,warrantyPeriod,customerId,requestDateStart,requestDateEnd,pageable);
	}

	// Get raised warranty requests for a specific customer with optional filters
	@Override
	public List<CompanyView> getRaisedWarrantyRequestsForCustomer(@RequestParam Integer userId,@RequestParam(required = false) Integer status,@RequestParam(required = false) String modelNo ){

		return companyviewrepository.findRaisedWarrantyRequestsForCustomerFiltered(userId, status, modelNo);
	}
	
	// Raise a warranty request
	@Override
	public PostResponse raiseWarrantyRequest(@RequestBody RaiseWarrantyPayload view) {
		
		  boolean modelExists = repository.existsByModelNo(view.getModel_no());
			PostResponse Pr=new PostResponse();

		    if (!modelExists) {
		        Pr.setStatusCode(409); // Conflict
		        Pr.setMessage("Model number doesnt exists. Cannot register again.");
		        return Pr;
		    }
		
		CompanyView cv=new CompanyView();
		cv.setCustomer_email(view.getCustomer_email());
		cv.setCustomer_id(view.getCustomer_id());
		cv.setCustomer_name(view.getCustomer_name());
		cv.setPhone_number(view.getPhone_number());
		cv.setPurchase_date(view.getPurchase_date());
		cv.setModel_no(view.getModel_no());
		cv.setProductImages(view.getProduct_images());
		cv.setRequest_date(view.getRequest_date());
		cv.setCompany_id(view.getCompany_id());
		cv.setReason(view.getReason());
		
		CompanyView f=companyviewrepository.save(cv);
		if(f.getCustomer_id()!=null) {
			Pr.setStatusCode(200);
			Pr.setMessage("Successfully updateed");
		}else {
			Pr.setMessage("Couldnt update");
			Pr.setStatusCode(404);
		}
	return Pr;   
	}

	// Get registered warranty requests by customer with optional model filter
    @Override
    public Page<CustomerDetails> getWarrantyRequests(@RequestParam Integer customerId, @RequestParam(required = false) String modelNo, Pageable pageable) {
    	return repository.findFilteredCustomerDetails(modelNo, customerId, pageable );
    }
    
    // Edit/update an already registered customer warranty
    @Override
    public CustomerDetails updateCustomer(Integer purchase_Id, CustomerDetails updatedCustomer) {
        Optional<CustomerDetails> optional = repository.findById(purchase_Id);
        if (optional.isPresent()) {
            CustomerDetails existing = optional.get();
            existing.setCustomerId(updatedCustomer.getCustomerId());
            existing.setModel_no(updatedCustomer.getModel_no());
            existing.setPurchase_date(updatedCustomer.getPurchase_date());
            return repository.save(existing);
        } else {
            throw new RuntimeException("Customer not found with ID: " + purchase_Id);
        }
    }

    // Soft delete a registered warranty
    @Transactional
    @Override
    public PostResponse deleteCustomer(Integer purchase_Id) {
//        Optional<CustomerDetails> optional = repository.findById(purchase_Id);
//        if (optional.isPresent()) {
//            CustomerDetails customer = optional.get();
//            customer.setIsDeleted(purchase_Id);
//            repository.save(customer);
//            return "Customer marked as deleted with ID: " + purchase_Id;
//        } else {
//            return "Customer not found with ID: " + purchase_Id;
//        }
    	
    	PostResponse k=new PostResponse();
    Integer p =	repository.deleteCustomer(purchase_Id);
    if(p>0) {
    	k.setMessage("Deleted");
    	k.setStatusCode(200);
    }else {
    k.setMessage("Cant Delete");
	k.setStatusCode(200);
    }
    
    return k;
    }
    
    // Soft delete a raised warranty request
    @Transactional
    public PostResponse deleteRaisedWarranty(@RequestParam Integer raised_Id) {
    	PostResponse k=new PostResponse();
        Integer p =	companyviewrepository.deleteRaisedWarranty(raised_Id);
        if(p>0) {
        	k.setMessage("Deleted");
        	k.setStatusCode(200);
        }else {
        k.setMessage("Cant Delete");
    	k.setStatusCode(200);
        }
        
        return k;
    }
    
    // Approve or reject warranty
    @Transactional
    @Override
    public PostResponse WarrantyAction(@RequestParam Integer purchase_id , @RequestParam Integer status, @RequestParam(required = false) String rejection_remarks) {
	    PostResponse response = new PostResponse();
	    
    	Integer p = companyviewrepository.WarrantyAction(purchase_id,status,rejection_remarks);
       	if(p>0) {
    		response.setStatusCode(200);
    		response.setMessage("Updated");
    	}else {
    		response.setStatusCode(404);
    		response.setMessage("Couldnt Update");
    	}
    	return response;
    }
   
}
