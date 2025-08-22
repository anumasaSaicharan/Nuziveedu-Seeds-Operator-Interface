package com.nsl.operatorInterface.entity;

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
@Table(name = "DUPLICATE_PRINT_CODES")
public class DuplicatePrintCodes {

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

	@Column(name = "UID_CODE")
	private String uidCode;

	@Column(name = "PRINT_JOB_ID")
	private String printJobId;

	public String getUidCode() {
		return uidCode;
	}

	public void setUidCode(String uidCode) {
		this.uidCode = uidCode;
	}

	public String getPrintJobId() {
		return printJobId;
	}

	public void setPrintJobId(String printJobId) {
		this.printJobId = printJobId;
	}

}
