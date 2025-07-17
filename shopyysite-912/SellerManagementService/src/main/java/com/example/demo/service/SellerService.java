package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.InventoryItem;
import com.example.demo.model.PurchaseTable;
import com.example.demo.repository.PurchaseRepository;
import com.example.demo.repository.SellerRepository;
import com.example.demo.response.PostResponse;

import jakarta.transaction.Transactional;

@Service
public class SellerService implements ISellerService {
	
	private SellerRepository sellerRepository;
	private PurchaseRepository purchaseRepository;
	public SellerService(SellerRepository sellerRepository,PurchaseRepository purchaseRepository) {
		this.sellerRepository=sellerRepository;
		this.purchaseRepository=purchaseRepository;
	}
	
	@Transactional
	public PostResponse PostInventory(InventoryItem inventoryItem) {
		InventoryItem item = sellerRepository.save(inventoryItem);
		PostResponse resp=new PostResponse();
		if(item!=null && item.getCompany_id()!=null) {
			resp.setStatusCode(200);
			resp.setMessage("Successfully Posted");
		}else {
			resp.setStatusCode(400);
			resp.setMessage("Couldnt save");
		}
		return resp;
	}
	
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
	
	@Override
	public Page<PurchaseTable> GetPurchases(Integer sellerId, String modelNo, Pageable pageable) {
	    return purchaseRepository.findFilteredPurchases(sellerId, modelNo, pageable);
	}

	@Override
	public Page<InventoryItem> GetAllInventory(Integer sellerId, Integer categoryId, String modelNo, Integer warranty, LocalDate purchaseDate, Pageable pageable) {
	    return sellerRepository.findByFilters(sellerId, categoryId, modelNo, warranty, purchaseDate, pageable);
	}


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
