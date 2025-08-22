package com.nsl.operatorInterface.entity;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "COMPANY_DETAILS")
public class CompanyDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false, updatable = false)
	private Long id;

	@Column(name = "ACTIVE", nullable = false)
	private Boolean active = Boolean.TRUE;

	@Column(name = "CREATED_ON", nullable = false, updatable = false)
	private Timestamp createdOn;

	@Column(name = "MODIFIED_ON")
	private Timestamp modifiedOn;

	@Column(name = "COMPANY_NAME", nullable = false, length = 50)
	private String companyName;

	@Column(name = "COMPANY_ID", nullable = false, length = 12)
	private String companyId;

	@Column(name = "SECURE_KEY", nullable = false, length = 50)
	private String secureKey;

	@Column(name = "PLANT_NAME", nullable = false, length = 40)
	private String plantName;

	@Column(name = "PLANT_ID", nullable = false, length = 3)
	private String plantId;

	@Column(name = "LINE_NAME", nullable = false, length = 40)
	private String lineName;

	@Column(name = "LINE_ID", nullable = false, length = 3)
	private String lineId;

	@Column(name = "STATUS")
	private String status;

}