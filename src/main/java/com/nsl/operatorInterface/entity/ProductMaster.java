package com.nsl.operatorInterface.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "PRODUCT_MASTER")
public class ProductMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "CREATED_ON", updatable = false)
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "PRODUCT_NAME", unique = true, nullable = false, length = 50)
	private String productName;

	@Column(name = "PACK_SIZE", unique = true, nullable = false, columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
	private BigDecimal packSize = BigDecimal.ZERO;

	@Column(name = "PACK_UNIT", unique = true, nullable = false, length = 5)
	private String packUnit;

	@Column(name = "GTIN_NUMBER", unique = true, nullable = false, length = 13)
	private String gtinNumber;

	@Column(name = "MRP", columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
	private BigDecimal mrp = BigDecimal.ZERO;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "UNIT_PRICE", columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
	private BigDecimal unitPrice = BigDecimal.ZERO;

	@Column(name = "PRODUCT_DESCRIPTION", length = 1000)
	private String productDescription;

	@Column(name = "PRODUCT_CODE", length = 10)
	private String productCode;

}
