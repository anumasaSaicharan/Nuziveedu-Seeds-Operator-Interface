package com.nsl.operatorInterface.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PrinterMasterRequest {

	@NotBlank(message = "Plant name mmust be less than 50 characters")
	private String plantName;

	@NotBlank(message = "Line name is mandatory")
	@Size(max = 50, message = "Line name must be less than 50 characters")
	private String lineName;

	@NotBlank(message = "Printer IP is mandatory")
	@Size(max = 50, message = "Printer IP must be less than 50 characters")
	private String printerIp;

	@NotNull(message = "Printer port is mandatory")
	private Integer printerPort;

	@NotBlank(message = "Printer name is mandatory")
	@Size(max = 100, message = "Printer name must be less than 100 characters")
	private String printerName;

	private boolean isAvailable = true;
	private boolean active = true;

	private String plantNumber;
	private String lineNumber;
}
