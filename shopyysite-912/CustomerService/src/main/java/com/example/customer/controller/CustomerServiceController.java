package com.example.customer.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.customer.model.CompanyView;
import com.example.customer.model.CustomerDetails;
import com.example.customer.service.ICustomerService;
import com.example.demo.payload.CustomerRegPayload;
import com.example.demo.payload.PostResponse;
import com.example.demo.payload.RaiseWarrantyPayload;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/")
public class CustomerServiceController {
	@Autowired
	private ICustomerService service;
	
	// Common method to handle validation errors and return meaningful messages
	private ResponseEntity<?> handleValidationErrors(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errors);
    }
	
	// Register a new customer warranty
	@PostMapping("/register-warranty")
	public ResponseEntity<?> registercustomer(@Valid @RequestBody CustomerRegPayload customerRegPayload,BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
            return handleValidationErrors(bindingResult);
        }
		PostResponse response= service.registercustomer(customerRegPayload);
		return ResponseEntity.ok(response);
	}
	
	// Get warranty requests of a customer
	@GetMapping("/warranty-requests-customer")
	public Page<CustomerDetails> getWarrantyRequests(
	        @RequestParam Integer customerId,
	        @RequestParam(required = false) String modelNo,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size) {

	    // Sanitize modelNo input
	    String modelNoSanitized = (modelNo == null || modelNo.trim().isEmpty()) ? null : modelNo.trim();
	    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "purchase_Id"));
	    return service.getWarrantyRequests(customerId, modelNoSanitized,pageable);
	}

	// Raise a new warranty request
	@PostMapping("/raise-warranty-request")
	public ResponseEntity<?> raiseWarrantyRequest(@Valid @RequestBody RaiseWarrantyPayload view, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
            return handleValidationErrors(bindingResult);
        }
		PostResponse response= service.raiseWarrantyRequest(view);
		return ResponseEntity.ok(response);
	}
	
	// Get all raised warranty requests for a customer
	@GetMapping("/raised-warranty-requests-customer")
	public List<CompanyView> getRaisedWarrantyRequestsForCustomer(
	    @RequestParam Integer userId,
	    @RequestParam(required = false) Integer status,
	    @RequestParam(required = false) String modelNo
	) {
	    // Sanitize modelNo to treat empty or blank input as null
	    String modelNoSanitized = (modelNo == null || modelNo.trim().isEmpty()) ? null : modelNo.trim();

	    return service.getRaisedWarrantyRequestsForCustomer(userId, status, modelNoSanitized);
	}

    // Get all raised warranty requests for a company with filters
	@GetMapping("/getraised-warranty-requests")
	public Page<CompanyView> getWarrayRequestsByCustomers(
	    @RequestParam Integer company_id, // required
	    @RequestParam(required = false) Integer status,
	    @RequestParam(required = false) String modelNo,
	    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate purchaseDate,
	    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate warrantyPeriod,
	    @RequestParam(required = false) Integer customerId,
	    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate requestDateStart,
	    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate requestDateEnd,
	    @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
	) {
	    // Sanitize String parameters
	    String modelNoSanitized = (modelNo == null || modelNo.trim().isEmpty()) ? "" : modelNo.trim();
	    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "warranty_request_id"));

	    return service.getWarrayRequestsByCustomers(
	        company_id,
	        status,
	        modelNoSanitized,
	        purchaseDate,
	        warrantyPeriod,
	        customerId,
	        requestDateStart,
	        requestDateEnd,
	        pageable
	    );
	}

	 // Edit an already registered warranty using purchase ID
	 @PostMapping("/editregistered-warranty")
	    public ResponseEntity<?> editCustomer(@RequestParam Integer purchase_Id,@Valid @RequestBody CustomerDetails updatedCustomer, BindingResult bindingResult) {
		 if (bindingResult.hasErrors()) {
	            return handleValidationErrors(bindingResult);
	        }
		 CustomerDetails cd= service.updateCustomer(purchase_Id, updatedCustomer);
		 PostResponse response= new PostResponse();

		 if(cd.getCustomerId()!=null) {
			 response.setStatusCode(200);
			 response.setMessage("Warranty registration updated");
		 }
		 else {
			 response.setStatusCode(400);
			 response.setMessage("Warranty registration can't be updated");
		 }
		 return ResponseEntity.ok(response);
	}
     
	// Delete a registered warranty using purchase ID
	 @PostMapping("/delete-registered-warranty")
	    public PostResponse deleteCustomer(@RequestParam Integer purchase_Id) {
	        return service.deleteCustomer(purchase_Id);
	 }
	 
	 // Delete a raised warranty request using raised ID
	 @PostMapping("/delete-raised-warranty-requests")
	 public PostResponse deleteRaisedWarranty(@RequestParam Integer raised_Id) {
		 return service.deleteRaisedWarranty(raised_Id);
	 }
	 
	 // Take action on a warranty (approve/reject) with optional rejection remarks
	 @GetMapping("/warranty-action")
	 public PostResponse WarrantyAction(@RequestParam Integer purchase_id,@RequestParam Integer status , @RequestParam(required = false) String rejection_remarks) {
		 return service.WarrantyAction(purchase_id,status,rejection_remarks);
	 }
}
