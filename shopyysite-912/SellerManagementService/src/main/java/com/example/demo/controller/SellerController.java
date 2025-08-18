package com.example.demo.controller;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.InventoryItem;
import com.example.demo.model.PurchaseTable;
import com.example.demo.response.BulkUploadResponse;
import com.example.demo.response.InventoryPost;
import com.example.demo.response.PostResponse;
import com.example.demo.service.ISellerService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/")
public class SellerController {

	@Autowired
	private ISellerService isellerservice;
	
	public SellerController(ISellerService isellerservice) {
		this.isellerservice=isellerservice;
	}
	
	// Add a new purchase entry
	@PostMapping("/purchase")
	public ResponseEntity<PostResponse> postPurchase(@Valid @RequestBody PurchaseTable purchaseItem, BindingResult bindingResult) {

	    PostResponse response = new PostResponse();

	    if (bindingResult.hasErrors()) {
	        String errorMessages = bindingResult.getFieldErrors().stream()
	            .map(err -> err.getField() + ": " + err.getDefaultMessage())
	            .collect(Collectors.joining("; ")); // Combine all messages into a single string

	        response.setStatusCode(400);
	        response.setMessage(errorMessages);

	        return ResponseEntity.badRequest().body(response);
	    }

	    response = isellerservice.PostPurchase(purchaseItem);
	    return ResponseEntity.ok(response);
	}
	
	// Upload multiple purchases using Excel file
	@PostMapping(value = "/bulkupload-purchase", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public BulkUploadResponse bulkUploadPurchase(@RequestParam("file") MultipartFile postedFile,@RequestParam Integer seller_id) {
	    return isellerservice.bulkUploadPurchase(postedFile,seller_id);
	}
	
	// Upload multiple inventory items using Excel file
	 @PostMapping(value = "/bulk-upload-inventory", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
     public BulkUploadResponse bulkUploadInventory(@RequestParam("file") MultipartFile file,
                                                      @RequestParam("seller_id") Integer sellerId) {
         return isellerservice.bulkUploadInventory(file, sellerId);
     }

	 // Add a single inventory item
	@PostMapping("/inventory")
	public ResponseEntity<?> PostInventory(@Valid @RequestBody InventoryPost inventoryItem, BindingResult bindingResult) {
	    if (bindingResult.hasErrors()) {
	        Map<String, String> errors = new HashMap<>();
	        bindingResult.getFieldErrors().forEach(error ->
	            errors.put(error.getField(), error.getDefaultMessage())
	        );
	        return ResponseEntity.badRequest().body(errors);
	    }

	    PostResponse response = isellerservice.PostInventory(inventoryItem);
	    return ResponseEntity.ok(response);
	}

	// Get list of purchases
	@GetMapping("/GetPurchases")
	public Page<PurchaseTable> GetPurchases(
	        @RequestParam(required = false) Integer Seller_Id,
	        @RequestParam(required = false) String modelNo,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "5") int size) {

	    String modelNoSanitized = (modelNo == null || modelNo.trim().isEmpty()) ? null : modelNo.trim();
	    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sale_id"));
	    return isellerservice.GetPurchases(Seller_Id, modelNoSanitized, pageable);
	}

	// Get list of inventory items
	@GetMapping("/allinventory")
	public Page<InventoryItem> GetAllInventory(
	        @RequestParam Integer Seller_Id,
	        @RequestParam(required = false) String categoryId,
	        @RequestParam(required = false) String modelNo,
	        @RequestParam(required = false) String warranty,
	        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate purchaseDate,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "5") int size) {

		Integer categoryIdInt = (categoryId == null || categoryId.trim().isEmpty()) ? null : Integer.valueOf(categoryId);
	    String modelNoSanitized = (modelNo == null || modelNo.trim().isEmpty()) ? null : modelNo.trim();
	    Integer warrantyInt = (warranty == null || warranty.trim().isEmpty()) ? null : Integer.valueOf(warranty);
	    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "purchase_id"));
	    return isellerservice.GetAllInventory(Seller_Id, categoryIdInt, modelNoSanitized, warrantyInt, purchaseDate, pageable);
	}


	// Edit an existing inventory item
	@PostMapping("/editinventory")
	public PostResponse editInventory(@RequestBody InventoryItem inventoryItem,@RequestParam Integer purchaseId) {
     return isellerservice.EditInventory(inventoryItem, purchaseId);
    }
	
	// Delete an inventory item
	@PostMapping("/deleteinventory")
	public PostResponse DeleteInventory(@RequestParam Integer purchase_id) {
		return isellerservice.DeleteInventory(purchase_id);
	}
	
	// Edit an existing purchase
	@PostMapping("/editpurchase")
	public PostResponse EditPurchase(@RequestBody PurchaseTable purchaseItem,@RequestParam Integer sale_id) {
		return isellerservice.EditPurchase(purchaseItem,sale_id);
	}
	
	// Delete a purchase
	@GetMapping("/deletepurchase")
	public PostResponse DeletePurchase(@RequestParam Integer sale_id) {
		return isellerservice.DeletePurchase(sale_id);
	}
	
	// Check if warranty request is valid using model number and phone number
	 @GetMapping("/warranty-reg-valid")
	 public Boolean WarrrantyReqValid(@RequestParam String ModelNo,@RequestParam String PhoneNo) {
		 return isellerservice.WarrrantyReqValid(ModelNo,PhoneNo);
	 }
	

}