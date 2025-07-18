package com.example.demo.service;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Base64;


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
	
	public BulkUploadResponse bulkUploadProducts(MultipartFile postedFile,@RequestParam Integer company_id) {
    BulkUploadResponse response = new BulkUploadResponse();
    List<ProductDetails> validProducts = new ArrayList<>();
    List<String> successRows = new ArrayList<>();
    List<String> failedRows = new ArrayList<>();
    
    response.setSuccessRecords(successRows);
    response.setFailedRecords(failedRows);

    // Validate file existence and type
    if (postedFile == null || postedFile.isEmpty()) {
        response.setStatusCode(400);
        response.setMessage("No file uploaded");
        return response;
    }

    String fileName = postedFile.getOriginalFilename();
    String extension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();

    if (!extension.equals(".xls") && !extension.equals(".xlsx")) {
        response.setStatusCode(415);
        response.setMessage("Only Excel files (.xls, .xlsx) are allowed");
        return response;
    }

    try (InputStream is = postedFile.getInputStream()) {
        Workbook workbook = extension.equals(".xls") ? new HSSFWorkbook(is) : new XSSFWorkbook(is);
        Sheet sheet = workbook.getSheetAt(0);
        
        if (sheet == null || sheet.getLastRowNum() < 1) {
            response.setStatusCode(400);
            response.setMessage("Empty Excel file");
            return response;
        }

        // Validate header row
        Row headerRow = sheet.getRow(0);
        if (!validateHeaderRow(headerRow)) {
            response.setStatusCode(400);
            response.setMessage("Invalid template format. Please download the latest template.");
            return response;
        }

        // Process data rows
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            String rowIdentifier = "Row " + (i + 1);
            try {
                ProductDetails product = parseProductRow(row, rowIdentifier,company_id);
                validateProduct(product, rowIdentifier);
                
                // Process image URL if present
                String imageUrls = getCellValue(row, 6); // Assuming column 8
                if (imageUrls != null && !imageUrls.trim().isEmpty()) {
                    String[] urlArray = imageUrls.split(",");
                    System.out.println("" + urlArray);  // log success

                    
                    for (String url : urlArray) {
                        if (!url.trim().isEmpty()) {
                            processProductImage(product, url.trim(), rowIdentifier,failedRows);
                        }
                    }
                }
                
                successRows.add(product.getProduct_name() + " - Ready for upload");
                validProducts.add(product);
            } catch (Exception e) {
                failedRows.add(rowIdentifier + ": " + e.getMessage());
            }
        }

        // Batch save valid products
        if (!validProducts.isEmpty()) {
            companyMgtRepository.saveAll(validProducts);
            successRows.replaceAll(s -> s.replace("Ready for upload", "Uploaded successfully"));
        }

        response.setStatusCode(200);
        response.setMessage(String.format(
            "Processed %d rows. Success: %d, Failed: %d",
            sheet.getLastRowNum(),
            validProducts.size(),
            failedRows.size()
        ));

    } catch (Exception e) {
        response.setStatusCode(500);
        response.setMessage("Error processing file: " + e.getMessage());
    }

    return response;
}

// Helper Methods
//to check if its a valid template
private boolean validateHeaderRow(Row headerRow) {
    if (headerRow == null) return false;
    
    return getCellValue(headerRow, 0).equalsIgnoreCase("Model_no") &&
           getCellValue(headerRow, 1).equalsIgnoreCase("Product_name") &&
           getCellValue(headerRow, 2).equalsIgnoreCase("Product_category") &&
           getCellValue(headerRow, 3).equalsIgnoreCase("Product_price") &&
           getCellValue(headerRow, 4).equalsIgnoreCase("Man_date") &&
           getCellValue(headerRow, 5).equalsIgnoreCase("Warrany_tenure") &&
           getCellValue(headerRow, 6).equalsIgnoreCase("Image_URL");
}
//to parse and return data in each row
private ProductDetails parseProductRow(Row row, String rowIdentifier,Integer company_id) throws Exception {
    ProductDetails product = new ProductDetails();
    
    try {
        product.setModel_no(getCellValue(row, 0));
        product.setProduct_name(getCellValue(row, 1));
        
//        product.setProduct_category(getCellValue(row, 2));
        String category = getCellValue(row, 2).trim();
        switch (category.toLowerCase()) {
            case "plastic":
                product.setProduct_category("2");
                break;
            case "electronics":
                product.setProduct_category("1");
                break;
            case "wood":
                product.setProduct_category("3");
                break;
            case "metal":
                product.setProduct_category("4");
                break;
            default:
                product.setProduct_category("unknown");
                break;
        }

        
        
        
        product.setProduct_price(parseIntSafe(getCellValue(row, 3), "Product Price"));
        
        String dateStr = getCellValue(row, 4);
        product.setMan_date(dateStr);
        
        product.setWarrany_tenure(parseIntSafe(getCellValue(row, 5), "Warranty Tenure"));
        product.setCompany_id(company_id);
        product.setProductImages(new ArrayList<>());
        
        return product;
    } catch (Exception e) {
        throw new Exception("Error parsing product data: " + e.getMessage());
    }
}
//cheking if warranty tenure is a valid date
private int parseIntSafe(String value, String fieldName) throws Exception {
    try {
        return Integer.parseInt(value);
    } catch (NumberFormatException e) {
        throw new Exception(fieldName + " must be a valid number");
    }
}
//adding validation so that making it mandatory modelNo product name fields are required
private void validateProduct(ProductDetails product, String rowIdentifier) throws Exception {
    if (product.getModel_no() == null || product.getModel_no().trim().isEmpty()) {
        throw new Exception("Model No is required");
    }
    
    if (product.getProduct_name() == null || product.getProduct_name().trim().isEmpty()) {
        throw new Exception("Product Name is required");
    }
    
    if (getProductDetailsByModelNo(product.getModel_no()) != null) {
        throw new Exception("Model No already exists: " + product.getModel_no());
    }
    
    // Add any additional validations here
}

private void processProductImage(ProductDetails product, String imageUrl, String rowIdentifier, List<String> failedRows) {
    try {
        String base64Image = downloadAndConvertToBase64(imageUrl);
        product.getProductImages().add(base64Image);
    } catch (Exception e) {
        System.out.println(e.getMessage()+"erroe occured");

        failedRows.add(rowIdentifier + ": Image download failed for URL " + imageUrl + " - " + e.getMessage());
    }
}

private String downloadAndConvertToBase64(String imageUrl) throws IOException {
    URL url = new URL(imageUrl);
    URLConnection connection = url.openConnection();
    connection.setConnectTimeout(115000); // 5 seconds
    connection.setReadTimeout(110000); // 10 seconds
    
    try (InputStream in = connection.getInputStream()) {
        byte[] imageBytes = in.readAllBytes();
        
        // Validate image size (max 8MB)
        if (imageBytes.length > 8_000_000) {
            throw new IOException("Image exceeds maximum size of 8MB");
        }
        
        // Validate image type
        String mimeType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(imageBytes));
        if (mimeType == null || !mimeType.startsWith("image/")) {
            throw new IOException("Invalid image type: " + mimeType);
        }
        
        return "data:" + mimeType + ";base64," + Base64.getEncoder().encodeToString(imageBytes);
    }
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

	
	//private String getCellValue(Row row, int cellNum) {
//  if (row == null) return null;
//  Cell cell = row.getCell(cellNum, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
//  if (cell == null) return null;
//  
//  switch (cell.getCellType()) {
//      case STRING:
//          return cell.getStringCellValue().trim();
//      case NUMERIC:
//          if (DateUtil.isCellDateFormatted(cell)) {
//              return cell.getLocalDateTimeCellValue().toLocalDate().toString();
//          }
//          return String.valueOf((int) cell.getNumericCellValue());
//      case BOOLEAN:
//          return String.valueOf(cell.getBooleanCellValue());
//      default:
//          return null;
//  }
//}



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