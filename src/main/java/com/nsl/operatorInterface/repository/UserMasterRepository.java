package com.nsl.operatorInterface.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nsl.operatorInterface.entity.UserMaster;

public interface UserMasterRepository extends JpaRepository<UserMaster, Long> {

	boolean existsByEmail(String email);

	boolean existsByMobileNumber(String mobileNumber);

	Optional<UserMaster> findByUserNameAndPasswordAndActiveTrue(String userName, String password);

}
