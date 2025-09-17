package com.example.signup.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.signup.dto.UserInfoDTO;
import com.example.signup.model.UserDetails;

import jakarta.transaction.Transactional;

@Repository
public interface SignupRepository extends JpaRepository<UserDetails, Integer>{
	// Find a user by their email address (used for login and validation)
	Optional<UserDetails> findByEmail(String email);
	// Get all users from the UserDetails table
	@Query("SELECT u FROM UserDetails u")
	public List<UserDetails>GetUsers();
	// Get user details for a specific user_id
	@Query("SELECT u FROM UserDetails u WHERE u.user_id = :user_Id")
	UserDetails GetUserDetails(@Param("user_Id") Integer user_Id);
	
	@Query("SELECT new com.example.signup.dto.UserInfoDTO(u.user_id, u.userName, u.email) FROM UserDetails u WHERE u.user_id IN :user_Id")
	List<UserInfoDTO> getUsernameByUserIds(@Param("user_Id") List<Integer> user_Id);

	@Query("SELECT u FROM UserDetails u WHERE (:userType IS NULL OR u.userType = :userType) AND u.userType <> 4")
	Page<UserDetails> getAllUsers(Pageable pageable, @Param("userType") Integer userType);

	@Modifying
	@Transactional
	@Query("UPDATE UserDetails u SET u.active_status = :actionstatus WHERE u.user_id = :user_id")
	Integer ChangeUserStatus(@Param("user_id") Integer user_id, @Param("actionstatus") Integer actionstatus);

	

}
