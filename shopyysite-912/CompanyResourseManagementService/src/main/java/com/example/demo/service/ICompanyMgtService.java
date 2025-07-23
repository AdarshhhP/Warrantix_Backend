package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.ProductDetails;
import com.example.demo.response.BulkUploadResponse;
import com.example.demo.response.PostResponse;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

public interface ICompanyMgtService {
	public PostResponse postProduct(ProductDetails productDetails);
	public Page<ProductDetails> getProducts(@RequestParam Integer companyId, Integer holderStatus, String productCategory,String ModelNo,
		    LocalDate manDate, Pageable pageable);
	public ProductDetails getProductDetailsByModelNo(@RequestParam String Model_no);
	public ProductDetails getProductDetailsByModelNoImage(@RequestParam String Model_no);
	public List<ProductDetails> getProductsByModelNos(@RequestParam List<String> modelNos);
	public PostResponse ChangeholderStatus(@RequestParam String Model_no,@RequestParam Integer status);
	public Boolean CheckEligibility(@RequestParam String Model_no,@RequestParam Integer checkvalue);
	public BulkUploadResponse bulkUploadProducts(@Parameter(description = "File containing product data", 
            schema = @Schema(type = "string", format = "binary"))
  @RequestParam("file") MultipartFile postedFile,@RequestParam Integer company_id);
}