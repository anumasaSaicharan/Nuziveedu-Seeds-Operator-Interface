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
@Table(name = "PRINTER_MASTER")
public class PrinterMaster {

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

	@Column(name = "PLANT_NAME", nullable = false, length = 50)
	private String plantName;

	@Column(name = "LINE_NAME", nullable = false, length = 50)
	private String lineName;

	@Column(name = "PRINTER_IP", nullable = false, length = 50)
	private String printerIp;

	@Column(name = "PRINTER_PORT", nullable = false)
	private Integer printerPort;

	@Column(name = "PRINTER_NAME", nullable = false, length = 100)
	private String printerName;

	@Column(name = "IS_AVAILABLE", nullable = false)
	private boolean isAvailable;

	@Column(name = "PLANT_NUMBER")
	private String plantNumber;

	@Column(name = "LINE_NUMBER")
	private String lineNumber;

}
