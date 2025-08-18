package com.example.demo.controller;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.service.annotation.PostExchange;

import com.example.demo.payload.ChangeItemStatus;
import com.example.demo.model.ProductDetails;
import com.example.demo.payload.UpdateSerialStatusRequest;
import com.example.demo.response.BulkUploadResponse;
import com.example.demo.response.PostResponse;
import com.example.demo.service.ICompanyMgtService;
 
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/")
public class CompanyMgtController {
	// Injecting the Company Management Service using constructor-based dependency injection
	@Autowired
	private  ICompanyMgtService service;
	// Constructor for injecting the service
	public CompanyMgtController(ICompanyMgtService service) {
		this.service=service;
	}
	
	// API to add a single product
	@PostMapping("/postproduct")
	public PostResponse PostProduct(@RequestBody ProductDetails productDetails) {
		return service.postProduct(productDetails);
	}
	
	// API for product bulk upload via Excel/CSV file
	@PostMapping(value = "/bulkupload-products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public BulkUploadResponse bulkUploadProducts(@RequestParam("file") MultipartFile postedFile,@RequestParam Integer company_id) {
	    return service.bulkUploadProducts(postedFile,company_id);
	}
 
	// API to fetch paginated product list with filters like company ID, holder status, category, model number, and manufacture date
	@GetMapping("/getProducts")
	public Page<ProductDetails> getProducts(
	        @RequestParam Integer company_id,
	        @RequestParam(required = false) Integer holderStatus,
	        @RequestParam(required = false) String productCategory,
	        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate manDate,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(required = false) String ModelNo
			)
	{
 
	    String sanitizedCategory = (productCategory == null || productCategory.trim().isEmpty()) ? null : productCategory.trim();
	    String sanitizedModelNo = (ModelNo == null || ModelNo.trim().isEmpty()) ? "" : ModelNo.trim();
 
 
	    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "prod_id"));
	    return service.getProducts(company_id, holderStatus, sanitizedCategory,sanitizedModelNo, manDate, pageable);
	}
	
	// API to fetch product details by model number
	@GetMapping("/getProductDetailsByModelNo")
	public ProductDetails getProductDetailsByModelNo(@RequestParam String Model_no) {
		return service.getProductDetailsByModelNo(Model_no);
	}
	
	// API to fetch product details by model number (excluding image)
	@GetMapping("/getProductDetailsByModelNoNoimage")
	public ProductDetails getProductDetailsByModelNoImage(@RequestParam String Model_no) {
		return service.getProductDetailsByModelNoImage(Model_no);
	}
	
	// API to fetch multiple product details based on a list of model numbers
	@PostMapping("/products/by-models")
	public List<ProductDetails> getProductsByModelNos(@RequestBody List<String> modelNos) {
	   return  service.getProductsByModelNos(modelNos);
	}
	
	// API to change the holder status of a product using model number and new status
	@PostMapping("/changeholderstatus")
	public PostResponse ChangeholderStatus(@RequestParam String Model_no,@RequestParam Integer status) {
		return service.ChangeholderStatus(Model_no,status);
	}
	
	// API to check product eligibility based on model number and a condition value
	@GetMapping("/checkeligibility")
	public Boolean CheckEligibility(@RequestParam String Model_no,@RequestParam Integer checkvalue) {
		return service.CheckEligibility(Model_no,checkvalue);
	}
	
	@GetMapping("/getProductDetailsByProductId")
	public ProductDetails getProductDetailsByProductId(@RequestParam Integer productId) {
		return service.getProductDetailsByProductId(productId);
	}
	@PostMapping("/changeserialStatus")
	public PostResponse ChangeMultipleSerialStatus(@RequestBody UpdateSerialStatusRequest requestbody) {
		return service.ChangeMultipleSerialStatus(requestbody);
	}
	
	@PostMapping("/changeitemstatus")
	public PostResponse ChangeItemStatus(@RequestBody ChangeItemStatus changeitemstatus) {
		return service.ChangeItemStatus(changeitemstatus);
	}
	
}