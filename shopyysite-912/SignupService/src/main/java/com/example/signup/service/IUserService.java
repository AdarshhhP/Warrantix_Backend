package com.example.signup.service;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.signup.dto.UserDTO;
import com.example.signup.dto.UserInfoDTO;
import com.example.signup.dto.UserInfoUserType;
import com.example.signup.dto.UserListResponse;
import com.example.signup.model.UserDetails;
import com.example.signup.model.payload.UserPayload;
import com.example.signup.model.response.LoginResponse;
import com.example.signup.model.response.SignInResponse;

import jakarta.validation.Valid;

public interface IUserService {
	public SignInResponse CreateUser(UserDetails userDetails);
	public SignInResponse SignIn(@Valid UserPayload userPayload);
	public LoginResponse Login(UserDTO usedto);
	public List<UserDetails>GetUsers();
	public UserDetails GetUserDetails(Integer user_Id);
	public List<UserInfoDTO> getUsernameByUserIds(@RequestBody List<Integer> user_Id);
	public UserListResponse getAllUsers( @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size);
}