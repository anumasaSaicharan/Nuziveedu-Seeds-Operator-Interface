package com.nsl.operatorInterface.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "USER_MASTER")
public class UserMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "CREATED_ON", updatable = false)
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "ACTIVE", nullable = false)
	private boolean active;
	
	@Column(name = "ROLE_ID")
	private Long roleId;

	@Column(name = "NAME", nullable = false, length = 100)
	private String name;
	
	@Column(name = "EMAIL", unique = true, nullable = false)
	private String email;

	@Column(name = "MOBILE_NUMBER", unique = true, nullable = false, length = 15)
	private String mobileNumber;

	@Column(name = "USERNAME", nullable = false)
	private String userName;

	@Column(name = "PASSWORD", nullable = false)
	private String password;

	@Column(name = "PROFILE_PICTURE")
	private String profilePicture;
}
