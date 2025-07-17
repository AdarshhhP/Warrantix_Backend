package com.example.demo.service;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.ProductDetails;
import com.example.demo.repository.CompanyMgtRepository;
import com.example.demo.response.BulkUploadResponse;
import com.example.demo.response.PostResponse;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.transaction.Transactional;

@Service
public class CompanyMgtService implements ICompanyMgtService {
	
	@Autowired
	private CompanyMgtRepository companyMgtRepository;
	public CompanyMgtService(CompanyMgtRepository companyMgtRepository) {
		this.companyMgtRepository=companyMgtRepository;
	}
	
	@Override
	public PostResponse postProduct(ProductDetails productDetails) {
	    PostResponse response = new PostResponse();
	    
	    try {
	        // The productDetails object now contains a list of base64 images
	        ProductDetails savedProduct = companyMgtRepository.save(productDetails);
	        
	        response.setStatusCode(200);
	        response.setMessage("Product created successfully with " + 
	                          savedProduct.getProductImages().size() + " images");
	    } catch (Exception e) {
	        response.setStatusCode(500);
	        response.setMessage("Error creating product: " + e.getMessage());
	    }
	    
	    return response;
	}
	
	public BulkUploadResponse bulkUploadProducts(MultipartFile postedFile) {
	BulkUploadResponse response = new BulkUploadResponse();
    List<ProductDetails> validProducts = new ArrayList<>();
    List<String> succesRows = new ArrayList<>();

    List<String> failedRows = new ArrayList<>();
    response.setSuccessRecords(succesRows);
    response.setFailedRecords(failedRows);

    if (postedFile == null || postedFile.isEmpty()) {
        response.setStatusCode(null);
        response.setMessage(null);
        return response;
    }

    String fileName = postedFile.getOriginalFilename();
    String extension = fileName.substring(fileName.lastIndexOf("."));

    if (!extension.equalsIgnoreCase(".xls") && !extension.equalsIgnoreCase(".xlsx")) {
        response.setStatusCode(null);
        response.setMessage("Successfully Uploaded");
        return response;
    }

    try (InputStream is = postedFile.getInputStream()) {
        Workbook workbook = extension.equalsIgnoreCase(".xls") ? new HSSFWorkbook(is) : new XSSFWorkbook(is);
        Sheet sheet = workbook.getSheetAt(0);
        Row firstRow = sheet.getRow(0);

        if (firstRow == null) {
            response.setStatusCode(null);
            response.setMessage("Empty Excel");
            return response;
        }

        // ✅ Validate header row (cell index matches expected header name)
        if (!getCellValue(firstRow, 0).equalsIgnoreCase("Model_no") ||
            !getCellValue(firstRow, 1).equalsIgnoreCase("Product_name") ||
            !getCellValue(firstRow, 2).equalsIgnoreCase("Product_category") ||
            !getCellValue(firstRow, 3).equalsIgnoreCase("Product_price") ||
            !getCellValue(firstRow, 4).equalsIgnoreCase("Man_date") ||
            !getCellValue(firstRow, 5).equalsIgnoreCase("Warrany_tenure") ||
            !getCellValue(firstRow, 6).equalsIgnoreCase("Company_id")) {
            
            response.setStatusCode(509);;
            response.setMessage("Invalid Template");
            return response;
        }

        // ✅ Parse data rows
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            try {
                ProductDetails product = new ProductDetails();
                product.setModel_no(getCellValue(row, 0));
                product.setProduct_name(getCellValue(row, 1));
                product.setProduct_category(getCellValue(row, 2));
                product.setProduct_price(Integer.parseInt(getCellValue(row, 3)));

                String dateStr = getCellValue(row, 4);
                LocalDate date = LocalDate.parse(dateStr); // You can add formatter if needed
                product.setMan_date(date);

                product.setWarrany_tenure(Integer.parseInt(getCellValue(row, 5)));
                product.setCompany_id(Integer.parseInt(getCellValue(row, 6)));

                product.setProductImages(new ArrayList<>());

             // Simple validations
                if (product.getModel_no() == null || product.getProduct_name() == null) {
                    throw new IllegalArgumentException("Invalid Model No or Product Name");
                } else {
                    if (getProductDetailsByModelNo(product.getModel_no()) != null) {
                        throw new IllegalArgumentException("Model No already exists: " + product.getModel_no());
                    }
                }
                succesRows.add(product.getProduct_name() + "Added Successfully");
                validProducts.add(product);
            } catch (Exception e) {
                failedRows.add("Row " + (i + 1) + ": " + e.getMessage());
            }
        }

        // ✅ Save valid products
        companyMgtRepository.saveAll(validProducts);

        // ✅ Response summary
        response.setStatusCode(200);
        response.setMessage("Upload completed. Success: " + validProducts.size() + ", Failed: " + failedRows.size());

        if (!failedRows.isEmpty()) {
            failedRows.forEach(System.out::println);
        }

    } catch (Exception e) {
        response.setStatusCode(500);
        response.setMessage("Error reading Excel: " + e.getMessage());
    }

    return response;
}

	
	private String getCellValue(Row row, int cellIndex) {
	    Cell cell = row.getCell(cellIndex);
	    if (cell == null) return "";

	    switch (cell.getCellType()) {
	        case STRING:
	            return cell.getStringCellValue().trim();

	        case NUMERIC:
	            if (DateUtil.isCellDateFormatted(cell)) {
	                // Handle Excel numeric date correctly
	                Date date = cell.getDateCellValue();
	                return new SimpleDateFormat("yyyy-MM-dd").format(date);
	            } else {
	                // Handle normal numeric (like price, tenure, etc.)
	                return String.valueOf((int) cell.getNumericCellValue());
	            }

	        case BOOLEAN:
	            return String.valueOf(cell.getBooleanCellValue());

	        case FORMULA:
	            return cell.getCellFormula();

	        default:
	            return "";
	    }
	}




	@Override
	public Page<ProductDetails> getProducts(Integer companyId, Integer holderStatus, String productCategory, String ModelNo, LocalDate manDate, Pageable pageable) {
		return companyMgtRepository.getProducts(companyId, holderStatus, productCategory,ModelNo, manDate, pageable);
	}



@Override
public ProductDetails getProductDetailsByModelNo(@RequestParam String Model_no) {
	return companyMgtRepository.getProductDetailsByModelNo(Model_no);
}

@Override
public List<ProductDetails> getProductsByModelNos(@RequestParam List<String> modelNos){
	return companyMgtRepository.getProductsByModelNos(modelNos);
}

@Override
public Boolean CheckEligibility(@RequestParam String Model_no, @RequestParam Integer checkvalue) {
    List<ProductDetails> mm = companyMgtRepository.CheckEligibility(Model_no, checkvalue);
    if (mm.size() > 0) {
        return true;
    } else {
        return false;
    }
}


@Transactional
@Override
public PostResponse ChangeholderStatus(@RequestParam String Model_no,@RequestParam Integer status) {
	Integer ff = companyMgtRepository.ChangeholderStatus(Model_no,status);
	PostResponse pr=new PostResponse();
	if(ff>0) {
		pr.setMessage("Updated");
		pr.setStatusCode(200);
	}else {
		pr.setMessage("Cant Update");
		pr.setStatusCode(404);
	}
	return pr;
}


}