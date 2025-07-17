package com.example.signup.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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


import com.example.signup.dto.UserDTO;
import com.example.signup.model.UserDetails;
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
	
	private ResponseEntity<?> handleValidationErrors(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errors);
    }

	@PostMapping("/signup")
	public ResponseEntity<?> signUp(@Valid @RequestBody UserPayload userPayload, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
	            return handleValidationErrors(bindingResult);
		}
		
		 SignInResponse response = service.SignIn(userPayload);
	        return ResponseEntity.ok(response);
	    
	}

	
	@PostMapping("/login")
	public ResponseEntity<?> Login(@Valid @RequestBody UserDTO usedto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
            return handleValidationErrors(bindingResult);
	}
	
	 LoginResponse response = service.Login(usedto);
        return ResponseEntity.ok(response);
	}
	
	@GetMapping("/getusers")
	public List<UserDetails>GetUsers(){
		return service.GetUsers();
	}
	
	@PostMapping("/createuser")
	public ResponseEntity<?> signUp1(@Valid @RequestBody UserDetails userDetails, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
	            return handleValidationErrors(bindingResult);
		}
		
		 SignInResponse response = service.CreateUser(userDetails);
	        return ResponseEntity.ok(response);
	    
	}
	
	@GetMapping("/getuserdetails")
	public UserDetails GetUserDetails(@RequestParam Integer user_Id) {
		return service.GetUserDetails(user_Id);
	}
}
