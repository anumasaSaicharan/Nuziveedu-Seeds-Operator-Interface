package com.nsl.operatorInterface.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PrintCodesRequest {

	private Long printJobId;
	private BigDecimal packSize;

	@NotBlank(message = "Printer IP is required")
	private String printerIp;

	@NotBlank(message = "Lot number is required")
	@Size(max = 50, message = "Lot number cannot exceed 50 characters")
	private String lotNo;

	@NotBlank(message = "Variety is required")
	@Size(max = 50, message = "Variety cannot exceed 50 characters")
	private String variety;

	@NotBlank(message = "Crop is required")
	@Size(max = 50, message = "Crop cannot exceed 50 characters")
	private String crop;

	@NotBlank(message = "Batch number is required")
	@Size(max = 50, message = "Batch number cannot exceed 50 characters")
	private String batchNumber;

	@NotBlank(message = "Product name is required")
	@Size(max = 100, message = "Product name cannot exceed 100 characters")
	private String productName;

	@NotNull(message = "Manufacture date is required")
	@PastOrPresent(message = "Manufacture date must be today or in the past")
	private LocalDate manufactureDate;

	@NotNull(message = "Expiry date is required")
	@Future(message = "Expiry date must be in the future")
	private LocalDate expiryDate;

	@NotNull(message = "Number of prints is required")
	@Min(value = 1, message = "Number of prints must be at least 1")
	private Integer qtySatchesToPrint;
}
