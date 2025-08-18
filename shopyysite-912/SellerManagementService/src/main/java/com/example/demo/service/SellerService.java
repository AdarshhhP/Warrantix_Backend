package com.example.demo.service;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.InventoryItem;
import com.example.demo.model.PurchaseTable;
import com.example.demo.repository.PurchaseRepository;
import com.example.demo.repository.SellerRepository;
import com.example.demo.response.BulkUploadResponse;
import com.example.demo.response.InventoryPost;
import com.example.demo.response.PostResponse;
import com.example.demo.response.ResponseModelData;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import jakarta.transaction.Transactional;

@Service
public class SellerService implements ISellerService {
	
	private SellerRepository sellerRepository;
	private PurchaseRepository purchaseRepository;
	public SellerService(SellerRepository sellerRepository,PurchaseRepository purchaseRepository) {
		this.sellerRepository=sellerRepository;
		this.purchaseRepository=purchaseRepository;
	}
	
	@Autowired
	private RestTemplate restTemplate;
	
	// Add single inventory item
	@Transactional
	public PostResponse PostInventory(InventoryPost inventoryItem) {
	    // Check if serial_no list is not empty
	    if (inventoryItem.getSerial_no() == null || inventoryItem.getSerial_no().isEmpty()) {
	        PostResponse resp = new PostResponse();
	        resp.setStatusCode(400);
	        resp.setMessage("At least one serial number is required");
	        return resp;
	    }

	    try {
	        int successCount = 0;
	        
	        // Create one InventoryItem for each serial number
	        for (String serialNo : inventoryItem.getSerial_no()) {
	            InventoryItem Initem = new InventoryItem();
	            Initem.setModel_no(inventoryItem.getModel_no());
	            Initem.setCompany_id(inventoryItem.getCompany_id());
	            Initem.setCategory_id(inventoryItem.getCategory_id());
	            Initem.setPurchase_date(inventoryItem.getPurchase_date());
	            Initem.setPrice(inventoryItem.getPrice());
	            Initem.setWarranty(inventoryItem.getWarranty());
	            Initem.setSeller_id(inventoryItem.getSeller_id());
	            Initem.setSerial_no(serialNo); // Convert String to Integer
	            Initem.setIs_deleted(0); 
	            Initem.setIs_sold(1);
	            

	            InventoryItem item = sellerRepository.save(Initem);
	            if (item != null && item.getCompany_id() != null) {
	                successCount++;
	            }
	        }

	        PostResponse resp = new PostResponse();
	        if (successCount == inventoryItem.getSerial_no().size()) {
	            resp.setStatusCode(200);
	            resp.setMessage("All items successfully posted");
	        } else if (successCount > 0) {
	            resp.setStatusCode(207); // Multi-status
	            resp.setMessage("Partially successful. Posted " + successCount + 
	                          " out of " + inventoryItem.getSerial_no().size() + " items");
	        } else {
	            resp.setStatusCode(400);
	            resp.setMessage("Couldn't save any items");
	        }
	        return resp;
	        
	    } catch (NumberFormatException e) {
	        PostResponse resp = new PostResponse();
	        resp.setStatusCode(400);
	        resp.setMessage("Invalid serial number format. Must be numeric");
	        return resp;
	    } catch (Exception e) {
	        PostResponse resp = new PostResponse();
	        resp.setStatusCode(500);
	        resp.setMessage("Error occurred: " + e.getMessage());
	        return resp;
	    }
	}
	
	// Upload inventory in bulk from Excel
	 @Override
     public BulkUploadResponse bulkUploadInventory(MultipartFile file, Integer sellerId) {
		 // Initialize the response and supporting lists
         BulkUploadResponse response = new BulkUploadResponse();
         List<InventoryItem> validItems = new ArrayList<>();  // Valid rows
         List<String> successRows = new ArrayList<>();		  // Success messages
         List<String> failedRows = new ArrayList<>(); 		 // Failed messages
         response.setSuccessRecords(successRows);
         response.setFailedRecords(failedRows);
         // Check if file is null or empty
         if (file == null || file.isEmpty()) {
             response.setStatusCode(400);
             response.setMessage("No file uploaded");
             return response;
         }
         // Get file name and extension	
         String fileName = file.getOriginalFilename();
         String extension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
         // Check for allowed Excel extensions
         if (!extension.equals(".xls") && !extension.equals(".xlsx")) {
             response.setStatusCode(415);
             response.setMessage("Only Excel files (.xls, .xlsx) are allowed");
             return response;
         }

         try (InputStream is = file.getInputStream()) {
        	// Load workbook based on file type
             Workbook workbook = extension.equals(".xls") ? new HSSFWorkbook(is) : new XSSFWorkbook(is);
             Sheet sheet = workbook.getSheetAt(0);
             // If sheet is empty
             if (sheet == null || sheet.getLastRowNum() < 1) {
                 response.setStatusCode(400);
                 response.setMessage("Empty Excel file");
                 return response;
             }
             // Validate header row (first row)
             Row headerRow = sheet.getRow(0);
             if (!validateHeaderRow(headerRow)) {
                 response.setStatusCode(400);
                 response.setMessage("Invalid template headers");
                 return response;
             }
             // Process data rows from 2nd row onwards
             for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                 Row row = sheet.getRow(i);
                 if (row == null) continue; // Skip empty rows

                 String rowLabel = "Row " + (i + 1);
                 try {
                	 // Parse row into InventoryItem object
                     InventoryItem item = parseInventoryRow(row, rowLabel, sellerId);
                     // Validate content of the row
                     validateItem(item, rowLabel);
                     // Check if item already exists (by model number)
                     if(validateModelNoPurchases(item.getModel_no())!=true) {
                     validItems.add(item); // Add to valid list
                     successRows.add(item.getModel_no() + " added successfully");
                     String url = "http://localhost:1089/changeholderstatus?Model_no=" + item.getModel_no() + "&status=" + 2;
	   	              restTemplate.postForObject(url, null, PostResponse.class);
                    
                    }
                     else {
                         failedRows.add(item.getModel_no()+" failed: " + "Item already exist");
                     }
                 } catch (Exception e) {
                	// Log parsing or validation errors for the row
                     failedRows.add(rowLabel + " failed: " + e.getMessage());
                 }
             }
            // Save all valid items to the database
             if (!validItems.isEmpty()) {
                 sellerRepository.saveAll(validItems);
             }
             // Final response message
             response.setStatusCode(200);
             response.setMessage(String.format("Upload completed. Success: %d, Failed: %d",
                     successRows.size(), failedRows.size()));

         } catch (Exception e) {
        	// Handle any unexpected errors
             response.setStatusCode(500);
             response.setMessage("Error reading Excel: " + e.getMessage());
         }

         return response;
     }
  
	 // // Check if a model_no already exists in inventory
     private boolean validateModelNoPurchases(String modelNo) {
         List<InventoryItem> inventoryItem = sellerRepository.findAll();
         
         return inventoryItem.stream()
                 .anyMatch(item -> item.getModel_no().equalsIgnoreCase(modelNo) && item.getIs_deleted() == 0);
  
     }
     
     // Check if Excel header for inventory is valid
     private boolean validateHeaderRow(Row row) {
         return getCellValue(row, 0).equalsIgnoreCase("Model_no") &&
                getCellValue(row, 1).equalsIgnoreCase("Warranty") &&
                getCellValue(row, 2).equalsIgnoreCase("Purchase_date") &&
                getCellValue(row, 3).equalsIgnoreCase("Price");
     }

     // Parse one row of Excel into InventoryItem object
     private InventoryItem parseInventoryRow(Row row, String rowLabel, Integer sellerId) throws Exception {
    	  String modelNo = getCellValue(row, 0);

    	    String url = "http://localhost:1089/getProductDetailsByModelNoNoimage?Model_no={modelNo}";

    	    ResponseModelData response = restTemplate.getForObject(url, ResponseModelData.class, modelNo);
    	    
    	    if (response == null || response.getCompany_id() == null) {
    	        throw new Exception("Product not found for model number: " + modelNo);
    	        
    	    }
         InventoryItem item = new InventoryItem();
         try {
             item.setModel_no(getCellValue(row, 0));
             item.setCompany_id(response.getCompany_id());
             item.setWarranty((int) Double.parseDouble(getCellValue(row, 1)));
             String dateStr = getCellValue(row, 2);
             item.setPurchase_date(LocalDate.parse(dateStr));
             item.setPrice((int) Double.parseDouble(getCellValue(row, 3)));
             String category=getCellValue(row, 2);
             if(category=="Plastic") {
                 item.setCategory_id(2);
             }else if(category=="Electronics") {
                 item.setCategory_id(1);
             }else if(category=="Wood") {
                 item.setCategory_id(3);
             }else if(category=="Metal"){
                 item.setCategory_id(4);
             }else {
                 item.setCategory_id(5);
             }
             item.setIs_deleted(0);
             item.setSeller_id(sellerId);
         } catch (Exception e) {
             throw new Exception("Invalid data: " + e.getMessage());
         }
         return item;
     }

     // Validate required fields of inventory
     private void validateItem(InventoryItem item, String rowLabel) throws Exception {
         if (item.getModel_no() == null || item.getModel_no().isEmpty()) {
             throw new Exception("Model_no is required");
         }
         if (item.getWarranty() == null || item.getWarranty() <= 0) {
             throw new Exception("Warranty must be positive");
         }
     }

//     private String getCellValue(Row row, int index) {
//         Cell cell = row.getCell(index);
//         if (cell == null) return "";
//         return switch (cell.getCellType()) {
//             case STRING -> cell.getStringCellValue().trim();
//             case NUMERIC -> DateUtil.isCellDateFormatted(cell)
//                 ? new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue())
//                 : String.valueOf(cell.getNumericCellValue());
//             case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
//             case FORMULA -> cell.getCellFormula();
//             default -> "";
//         };
//     }
	
	// Upload purchase data from Excel in bulk
	@Transactional
	public BulkUploadResponse bulkUploadPurchase(@RequestParam("file") MultipartFile postedFile, @RequestParam Integer seller_id) {
	    BulkUploadResponse response = new BulkUploadResponse();
	    List<PurchaseTable> validPurchases = new ArrayList<>();
	    List<String> successRows = new ArrayList<>();
	    List<String> failedRows = new ArrayList<>();

	    response.setSuccessRecords(successRows);
	    response.setFailedRecords(failedRows);
	    // Check if file is null or empty
	    if (postedFile == null || postedFile.isEmpty()) {
	        response.setStatusCode(400);
	        response.setMessage("No file uploaded");
	        return response;
	    }
	    // Get file extension and check if it's Excel
	    String fileName = postedFile.getOriginalFilename();
	    String extension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
	    if (!extension.equals(".xls") && !extension.equals(".xlsx")) {
	        response.setStatusCode(415);
	        response.setMessage("Only Excel files (.xls, .xlsx) are allowed");
	        return response;
	    }

	    try (InputStream is = postedFile.getInputStream()) {
	    	// Create workbook depending on file type
	        Workbook workbook = extension.equals(".xls") ? new HSSFWorkbook(is) : new XSSFWorkbook(is);
	        Sheet sheet = workbook.getSheetAt(0); // Get first sheet
	        // Check for empty sheet
	        if (sheet == null || sheet.getLastRowNum() < 1) {
	            response.setStatusCode(400);
	            response.setMessage("Empty Excel file");
	            return response;
	        }

	        Row headerRow = sheet.getRow(0);
	        if (!validatePurchaseHeader(headerRow)) {
	            response.setStatusCode(400);
	            response.setMessage("Invalid template format. Please download the latest template.");
	            return response;
	        }
	        // Loop through rows starting from row 1
	        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
	            Row row = sheet.getRow(i);
	            if (row == null) continue;

	            String rowIdentifier = "Row " + (i + 1);
	            try {
	            	// Parse and validate the row
	                PurchaseTable purchase = parsePurchaseRow(row, rowIdentifier, seller_id);
	                validatePurchase(purchase, rowIdentifier);
	                // Check if model number exists in inventory
	               if( validateModelNos(purchase.getModelNo())==true) {
	            	// Check if model number is already sold (exists in PurchaseTable)
	            	   if(validateModelNoPurchase(purchase.getModelNo())==true) {
			                failedRows.add(purchase.getModelNo() + ": "+"This Model No already marked as sold");
	            	   }else {
	            		// Add to valid list
	            	    successRows.add(purchase.getModelNo() + " - Ready for upload");
	   	                validPurchases.add(purchase);
	   	              String url = "http://localhost:1089/changeholderstatus?Model_no=" + purchase.getModelNo() + "&status=" + 3;
	   	              restTemplate.postForObject(url, null, PostResponse.class);
	            	   }
	                }
	               else {
		                failedRows.add(purchase.getModelNo() + ": "+"This Model No not present in inventory");
	               }
	            } catch (Exception e) {
	                failedRows.add(rowIdentifier + ": " + e.getMessage());
	            }
	        }
	        // Save all valid purchase records to database
	        if (!validPurchases.isEmpty()) {
	            purchaseRepository.saveAll(validPurchases);
	            successRows.replaceAll(s -> s.replace("Ready for upload", "Uploaded successfully"));
	        }
	        // Set final response message and status
	        response.setStatusCode(200);
	        response.setMessage(String.format(
	            "Processed %d rows. Success: %d, Failed: %d",
	            sheet.getLastRowNum(),
	            validPurchases.size(),
	            failedRows.size()
	        ));

	    } catch (Exception e) {
	        response.setStatusCode(500);
	        response.setMessage("Error processing file: " + e.getMessage());
	    }

	    return response;
	}
	
	// Check if the model number is already sold
	private boolean validateModelNoPurchase(String modelNo) {
	    List<PurchaseTable> purchaseItems = purchaseRepository.findAll();
	    
	    return purchaseItems.stream()
	            .anyMatch(item -> item.getModelNo().equalsIgnoreCase(modelNo) && item.getIs_deleted() == 0);

	}
	
	// Check if model exists in inventory
	private boolean validateModelNos(String modelNo) {
	    List<InventoryItem> inventoryItems = sellerRepository.findAll();

	    return inventoryItems.stream()
	            .anyMatch(item -> item.getModel_no().equalsIgnoreCase(modelNo) && item.getIs_deleted() == 0);
	}

    // Validate purchase Excel header
	private boolean validatePurchaseHeader(Row headerRow) {
	    if (headerRow == null) return false;
	    return getCellValue(headerRow, 0).equalsIgnoreCase("Model_no") &&
	           getCellValue(headerRow, 1).equalsIgnoreCase("Price") &&
	           getCellValue(headerRow, 2).equalsIgnoreCase("Purchase_date") &&
	           getCellValue(headerRow, 3).equalsIgnoreCase("Warranty") &&
	           getCellValue(headerRow, 4).equalsIgnoreCase("Name") &&
	           getCellValue(headerRow, 5).equalsIgnoreCase("Email") &&
	           getCellValue(headerRow, 6).equalsIgnoreCase("Phono");
	}

	// Parse one row of Excel into PurchaseTable object
	private PurchaseTable parsePurchaseRow(Row row, String rowIdentifier, Integer seller_id) throws Exception {
	    PurchaseTable purchase = new PurchaseTable();

	    try {
	        purchase.setModelNo(getCellValue(row, 0));
	        purchase.setPrice(parseIntSafe(getCellValue(row, 1), "Price"));

	        String dateStr = getCellValue(row, 2);
	        purchase.setPurchase_date(LocalDate.parse(dateStr)); // Format: yyyy-MM-dd (from getCellValue)

	        purchase.setWarranty(parseIntSafe(getCellValue(row, 3), "Warranty"));
	        purchase.setName(getCellValue(row, 4));
	        purchase.setEmail(getCellValue(row, 5));
	        purchase.setPhono(getCellValue(row, 6));
	        purchase.setSeller_id(seller_id);
	        purchase.setIs_deleted(0);

	        return purchase;
	    } catch (Exception e) {
	        throw new Exception("Error parsing purchase data: " + e.getMessage());
	    }
	}

	// Validate fields of Purchase
	private void validatePurchase(PurchaseTable purchase, String rowIdentifier) throws Exception {
	    if (purchase.getModelNo() == null || purchase.getModelNo().trim().isEmpty()) {
	        throw new Exception("Model No is required");
	    }

	    if (purchase.getEmail() == null || !purchase.getEmail().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
	        throw new Exception("Invalid email format: " + purchase.getEmail());
	    }

	    if (purchase.getPrice() == null || purchase.getPrice() <= 0) {
	        throw new Exception("Price must be a positive number");
	    }

	    if (purchase.getWarranty() == null || purchase.getWarranty() <= 0) {
	        throw new Exception("Warranty must be a positive number");
	    }

	    if (purchase.getPurchase_date() == null) {
	        throw new Exception("Purchase date is required and must be in yyyy-MM-dd format");
	    }
	}

	// Parse a string into integer with validation
	private int parseIntSafe(String value, String fieldName) throws Exception {
	    try {
	        return Integer.parseInt(value);
	    } catch (NumberFormatException e) {
	        throw new Exception(fieldName + " must be a valid number");
	    }
	}

	// Extract value from Excel cell
	private String getCellValue(Row row, int cellIndex) {
	    Cell cell = row.getCell(cellIndex);
	    if (cell == null) return "";

	    switch (cell.getCellType()) {
	        case STRING:
	            return cell.getStringCellValue().trim();
	        case NUMERIC:
	            if (DateUtil.isCellDateFormatted(cell)) {
	                Date date = cell.getDateCellValue();
	                return new SimpleDateFormat("yyyy-MM-dd").format(date);
	            } else {
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

	// Post single purchase record
	@Override
    public PostResponse PostPurchase(PurchaseTable purchaseItem) {
        PostResponse response = new PostResponse();

        try {
            PurchaseTable saved = purchaseRepository.save(purchaseItem);
            if (saved != null && saved.getSale_id() != null) {
                response.setStatusCode(200);
                response.setMessage("Purchase saved successfully");
            } else {
                response.setStatusCode(400);
                response.setMessage("Failed to save purchase");
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error: " + e.getMessage());
        }

        return response;
    }
	
	// Get paginated purchase list with optional filters
	@Override
	public Page<PurchaseTable> GetPurchases(Integer sellerId, String modelNo, Pageable pageable) {
	    return purchaseRepository.findFilteredPurchases(sellerId, modelNo, pageable);
	}

	// Get paginated inventory list with filters
	@Override
	public Page<InventoryItem> GetAllInventory(Integer sellerId, Integer categoryId, String modelNo, Integer warranty, LocalDate purchaseDate, Pageable pageable) {
	    return sellerRepository.findByFilters(sellerId, categoryId, modelNo, warranty, purchaseDate, pageable);
	}

    // Update an existing inventory record
	@Transactional
	public PostResponse EditInventory(InventoryItem newItem, Integer purchaseId) {
	    PostResponse resp = new PostResponse();

	    Optional<InventoryItem> existingOpt = sellerRepository.findById(purchaseId);
	    if (existingOpt.isPresent()) {
	        InventoryItem existing = existingOpt.get();

	        // Update all fields except the ID
	        existing.setModel_no(newItem.getModel_no());
	        existing.setCompany_id(newItem.getCompany_id());
	        existing.setCategory_id(newItem.getCategory_id());
	        existing.setPurchase_date(newItem.getPurchase_date());
	        existing.setPrice(newItem.getPrice());
	        existing.setWarranty(newItem.getWarranty());
	        existing.setImage(newItem.getImage());
	        existing.setSeller_id(newItem.getSeller_id());

	        sellerRepository.save(existing);
	        resp.setStatusCode(200);
	        resp.setMessage("Inventory updated successfully.");
	    } else {
	        resp.setStatusCode(404);
	        resp.setMessage("Inventory with purchase_id " + purchaseId + " not found.");
	    }

	    return resp;
	}
	
	// Soft delete an inventory record
	@Transactional
	public PostResponse DeleteInventory(@RequestParam Integer purchase_id) {
	    int rowsAffected = sellerRepository.DeleteInventory(purchase_id);
	    
	    PostResponse response = new PostResponse();
	    if (rowsAffected > 0) {
	        response.setStatusCode(200);
	        response.setMessage("Inventory item soft-deleted successfully.");
	    } else {
	        response.setStatusCode(404);
	        response.setMessage("No inventory item found with the given ID.");
	    }

	    return response;
	}
	
	// Update an existing purchase
	@Transactional
	public PostResponse EditPurchase(@RequestBody PurchaseTable purchaseItem, @RequestParam Integer sale_id) {
	    PostResponse resp = new PostResponse();

	    Optional<PurchaseTable> existingOpt = purchaseRepository.findById(sale_id);

	    if (existingOpt.isPresent()) {
	        PurchaseTable existing = existingOpt.get();

	        // Update all fields
//	        existing.setCustomer_id(purchaseItem.getCustomer_id());
	        existing.setModelNo(purchaseItem.getModelNo());
	        existing.setPrice(purchaseItem.getPrice());
	        existing.setPurchase_date(purchaseItem.getPurchase_date());
	        existing.setWarranty(purchaseItem.getWarranty());
	        existing.setName(purchaseItem.getName());
	        existing.setEmail(purchaseItem.getEmail());
	        existing.setPhono(purchaseItem.getPhono());
	        existing.setSeller_id(purchaseItem.getSeller_id());

	        // Save the updated entity
	        purchaseRepository.save(existing);

	        resp.setStatusCode(200);
	        resp.setMessage("Purchase updated successfully.");
	    } else {
	        resp.setStatusCode(404);
	        resp.setMessage("Purchase with sale_id " + sale_id + " not found.");
	    }

	    return resp;
	}
	
	// Soft delete a purchase record
	@Transactional
	public PostResponse DeletePurchase(@RequestParam Integer sale_id) {
		int rows = purchaseRepository.DeletePurchase(sale_id);
		 PostResponse response = new PostResponse();
		    if (rows > 0) {
		        response.setStatusCode(200);
		        response.setMessage("Inventory item soft-deleted successfully.");
		    } else {
		        response.setStatusCode(404);
		        response.setMessage("No inventory item found with the given ID.");
		    }
		    return response;
		
	}
	
	// Check if a model number + phone exists in purchases (used for warranty request)
	 @Override
	    public Boolean WarrrantyReqValid(@RequestParam String ModelNo,@RequestParam String PhoneNo) {
		int exists = purchaseRepository.WarrrantyReqValid(ModelNo,PhoneNo);
		if(exists>0) {
			return true;
		}else {
			return false;
		}
	    }
	
}
