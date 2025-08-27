package com.nsl.operatorInterface.request;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonIgnoreProperties
public class PrintCodesRequest {

	private Long id;
	private Long printJobId;
	private Long productId;
	private Long cropId;
	private Long treatmentsId;

	@NotBlank(message = "Template Name is required")
	@Size(max = 100, message = "Template name cannot exceed 100 characters")
	private String templateName;

	@NotBlank(message = "Production Order Number is required")
	@Size(max = 50, message = "Production order number cannot exceed 50 characters")
	private String productionOrderNo;

	@NotBlank(message = "Printer IP is required")
	@Size(max = 50, message = "Printer IP cannot exceed 50 characters")
	private String printerIp;

	@NotBlank(message = "Lot number is required")
	@Size(max = 50, message = "Lot number cannot exceed 50 characters")
	private String lotNo;

	@NotBlank(message = "Variety is required")
	@Size(max = 50, message = "Variety cannot exceed 50 characters")
	private String variety;

	@NotBlank(message = "Batch number is required")
	@Size(max = 50, message = "Batch number cannot exceed 50 characters")
	private String batchNumber;

	@NotNull(message = "Manufacture date is required")
	@PastOrPresent(message = "Manufacture date must be today or in the past")
	private LocalDate manufactureDate;

	@NotNull(message = "Expiry date is required")
	@Future(message = "Expiry date must be in the future")
	private LocalDate expiryDate;

	@NotNull(message = "Number of prints is required")
	@Min(value = 1, message = "Number of prints must be at least 1")
	private Integer qtySatchesToPrint;

	@NotNull(message = "Pack size is required")
	private BigDecimal packSize;

	@NotBlank(message = "Use short URL is required")
	private String useShortUrl;

	@NotNull(message = "MRP is required")
	private BigDecimal mrp;
	
	@NotBlank(message = "Pack Unit is required")
	private String packUnit;

	@NotNull(message = "Unit price is required")
	private BigDecimal unitPrice;

//	@NotBlank(message = "Product name is required")
//	@Size(max = 100, message = "Product name cannot exceed 100 characters")
//	private String productName;

//	@NotBlank(message = "Crop is required")
//	@Size(max = 50, message = "Crop cannot exceed 50 characters")
//	private String crop;

//	private String companyCode;
//	private String plantCode;
//	private String lineCode;

//	private String materialSuffix;
//	private String cultivationRec;
//	private String dateOfTest;

//	private Integer bestBeforeMnths;

//	private String serialNumber;
//	private String status;
//	private String printerLine;

//	private Timestamp modifiedOn;

}
