package com.example.signup.service;


import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.signup.dto.UserDTO;
import com.example.signup.dto.UserInfoDTO;
import com.example.signup.jwt.JwtUtil;
import com.example.signup.model.UserDetails;
import com.example.signup.model.payload.UserPayload;
import com.example.signup.model.response.LoginResponse;
import com.example.signup.model.response.SignInResponse;
import com.example.signup.repository.SignupRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService implements IUserService {

    private final SignupRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    // Constructor injection of repository and JWT utility
    public UserService(SignupRepository userRepository,JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtUtil = jwtUtil;
    }

    // User registration (SignUp) logic
    @Transactional
    @Override
    public SignInResponse SignIn(UserPayload userPaylod) {
    	// Encrypt password before saving
    	userPaylod.setPassword(passwordEncoder.encode(userPaylod.getPassword()));
        SignInResponse response = new SignInResponse();
        UserDetails ud=new UserDetails();
        ud.setUserName(userPaylod.getUserName());
        ud.setEmail(userPaylod.getEmail());
        ud.setPassword(userPaylod.getPassword());
        ud.setUserType(1);
        // Save user to the database
        UserDetails savedUser = userRepository.save(ud);
        // Build response based on save success
        if (savedUser != null && savedUser.getUserId() != null) {
            response.setMessage("Signup success");
            response.setStatusCode(200);
        } else {
            response.setMessage("Signup failed");
            response.setStatusCode(400);
        }

        return response;
    }

    // User login logic with JWT generation
    @Override
    public LoginResponse Login(UserDTO userDto) {
    	// Find user by email
        UserDetails user = userRepository.findByEmail(userDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        LoginResponse obj=new LoginResponse();
        // Compare raw password with encoded password
        if (passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
            String token = jwtUtil.generateToken(user.getEmail());
            System.out.println("JWT Token: " + token); // or return it in response
            obj.setJwt(token);
            obj.setStatusCode(200);
           // obj.setMessage("Logged In");
            obj.setUser_type(user.getUserType());
            obj.setUser_id(user.getUserId());
            obj.setUser_name(user.getUserName());
            return obj;
        }else {
        	obj.setJwt("");
            obj.setStatusCode(400);
            //obj.setMessage("Invalid credentials");
            obj.setUser_type(null);
            return obj;
        }
    }
    
    // Get all users
    @Override
	public List<UserDetails>GetUsers(){
		return userRepository.GetUsers();
	}

    // Create user
	@Override
	public SignInResponse CreateUser(UserDetails userDetails) {
		userDetails.setPassword(passwordEncoder.encode(userDetails.getPassword()));
		UserDetails savedUser= userRepository.save(userDetails);
		SignInResponse response = new SignInResponse();
		if (savedUser != null && savedUser.getUserId() != null) {
            response.setMessage("Signup success");
            response.setStatusCode(200);
        } else {
            response.setMessage("Signup failed");
            response.setStatusCode(400);
        }

        return response;
		
	}
	
	// Get a user's details by user_id
	@Override
	public UserDetails GetUserDetails(Integer user_Id) {
		return userRepository.GetUserDetails(user_Id);
	}
	
	
	@Override
	public List<UserInfoDTO> getUsernameByUserIds(@RequestBody List<Integer> user_Id) {
		return userRepository.getUsernameByUserIds(user_Id);
	}
}
