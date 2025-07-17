package com.example.signup.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.signup.model.UserDetails;

@Repository
public interface SignupRepository extends JpaRepository<UserDetails, Integer>{
	Optional<UserDetails> findByEmail(String email);
	
	@Query("SELECT u FROM UserDetails u")
	public List<UserDetails>GetUsers();
	
	@Query("SELECT u FROM UserDetails u WHERE u.user_id = :user_Id")
	UserDetails GetUserDetails(@Param("user_Id") Integer user_Id);
}
