package com.example.signup.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.signup.dto.UserDTO;
import com.example.signup.dto.UserInfoDTO;
import com.example.signup.dto.UserInfoUserType;
import com.example.signup.dto.UserListResponse;
import com.example.signup.model.UserDetails;
import com.example.signup.model.payload.EmailRequest;
import com.example.signup.model.payload.UserPayload;
import com.example.signup.model.response.LoginResponse;
import com.example.signup.model.response.SignInResponse;
import com.example.signup.service.IUserService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/")
public class SignupController {
	@Autowired
	private  IUserService service;
	
//	 @Autowired
//	 private JavaMailSender mailSender;
	
	// Helper method to return validation errors in a readable format
	private ResponseEntity<?> handleValidationErrors(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errors);
    }

	// Sign up endpoint using UserPayload
	@PostMapping("/signup")
	public ResponseEntity<?> signUp(@Valid @RequestBody UserPayload userPayload, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
	            return handleValidationErrors(bindingResult);
		}
		
		 SignInResponse response = service.SignIn(userPayload);
	        return ResponseEntity.ok(response);
	    
	}

	// Login endpoint using UserDTO
	@PostMapping("/login")
	public ResponseEntity<?> Login(@Valid @RequestBody UserDTO usedto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
            return handleValidationErrors(bindingResult);
	}
	
	 LoginResponse response = service.Login(usedto);
        return ResponseEntity.ok(response);
	}
	
	// Get all registered users
	@GetMapping("/getusers")
	public List<UserDetails>GetUsers(){
		return service.GetUsers();
	}
	
	// User creation 
	@PostMapping("/createuser")
	public ResponseEntity<?> signUp1(@Valid @RequestBody UserDetails userDetails, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
	            return handleValidationErrors(bindingResult);
		}
		
		 SignInResponse response = service.CreateUser(userDetails);
	        return ResponseEntity.ok(response);
	    
	}
	
	// Get a single user's details by user ID
	@GetMapping("/getuserdetails")
	public UserDetails GetUserDetails(@RequestParam Integer user_Id) {
		return service.GetUserDetails(user_Id);
	}
	
	@PostMapping("/getusername")
	public List<UserInfoDTO> getUsernameByUserIds(@RequestBody List<Integer> user_Id) {
	   return service.getUsernameByUserIds(user_Id);
	}
	
	@GetMapping("/getallusers")
	public UserListResponse getAllUsers( @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,@RequestParam(required = false) String userType){
		return service.getAllUsers(page,size,userType);
	}
	
//	@PostMapping("/send")
//    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest request) {
//        try {
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setTo(request.getTo());
//            message.setSubject(request.getSubject());
//            message.setText(request.getBody());
//            mailSender.send(message);
//            return ResponseEntity.ok("Email sent successfully!");
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("Error sending email.");
//        }
//    }
	
	@GetMapping("/changeuser_status")
	public Boolean ChangeUserStatus(@RequestParam Integer user_id,@RequestParam Integer actionstatus) {
		return service.ChangeUserStatus(user_id,actionstatus);
	}
	
}
