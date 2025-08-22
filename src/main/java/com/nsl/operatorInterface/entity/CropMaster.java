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
@Table(name = "CROP_MASTER")
public class CropMaster {

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

	@Column(name = "NAME", nullable = false, length = 50)
	private String name;

}