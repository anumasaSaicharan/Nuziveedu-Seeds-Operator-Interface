package com.nsl.operatorInterface.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "UNIQUE_CODE_PRINTED_DATA_DETAILS")
public class UniqueCodePrintedDataDetails {

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

	@Column(name = "PRODUCTION_ORDER_NO", nullable = false, length = 50)
	private String productionOrderNo;

	@Column(name = "CROP_NAME", columnDefinition = "NVARCHAR(50)")
	private String cropName;

	@Column(name = "VARIETY", columnDefinition = "NVARCHAR(50)")
	private String variety;

	@Column(name = "UID_CODE", nullable = false, unique = true, length = 14)
	private String uidCode;

	@Column(name = "URL")
	private String url;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "SERIAL_NUMBER", unique = true, nullable = false)
	private Long serialNumber;

	@Column(name = "IS_SYNC", nullable = false)
	private boolean isSync;

	@Column(name = "PLANT_NAME", length = 50)
	private String plantName;

	@Column(name = "PLANT_NUMBER", length = 3)
	private String plantNumber;

	@Column(name = "LINE_NAME", length = 50)
	private String lineName;

	@Column(name = "LINE_NUMBER", length = 3)
	private String lineNumber;

	@Column(name = "USED", nullable = false)
	private boolean used;

	@Column(name = "USED_DATE", length = 30)
	private Timestamp usedDate;

	@Column(name = "PRINTED_ON")
	private Timestamp printedOn;

	@OneToOne
	@JoinColumn(name = "PRODUCT_ID", columnDefinition = "bigint")
	private ProductMaster productMaster;

	@Column(name = "PRODUCT_NAME", nullable = false, length = 50)
	private String productName;

	@Column(name = "PACK_SIZE", nullable = false, precision = 10, scale = 2)
	private BigDecimal packSize = BigDecimal.ZERO;

	@Column(name = "PACK_UNIT", nullable = false, length = 5)
	private String packUnit;

	@Column(name = "GTIN_NUMBER", nullable = false, length = 13)
	private String gtinNumber;

	@Column(name = "BATCH_NUMBER", nullable = false, length = 16)
	private String batchNumber;

	@Column(name = "MANUFACTURE_DATE", nullable = false)
	private LocalDate manufactureDate; // ⚠️ Can refactor to LocalDateTime

	@Column(name = "EXPIRY_DATE", nullable = false)
	private LocalDate expiryDate; // ⚠️ Can refactor to LocalDateTime

	@Column(name = "MRP", precision = 10, scale = 2)
	private BigDecimal mrp = BigDecimal.ZERO;

	@Column(name = "UNIT_PRICE", precision = 10, scale = 2)
	private BigDecimal unitPrice = BigDecimal.ZERO;

	@Column(name = "QTY_SATCHES_TO_PRINT", nullable = false)
	private int qtySatchesToPrint;

	@Column(name = "START_TIME", nullable = false)
	private LocalDateTime startTime;

	@Column(name = "END_TIME", nullable = false)
	private LocalDateTime endTime;

	@Column(name = "NO_OF_SATCHES_PRINTED", nullable = false)
	private int noOfSachesPrinted;

	@Column(name = "THREAD_ID")
	private long threadId;

	@Column(name = "PRINTING_STATUS")
	private String printingStatus;

	@Column(name = "USER_ID")
	private String userId;

	@Column(name = "SYNC_QTY")
	private int syncQty;

	@Column(name = "USE_SHORT_URL")
	private String useShortUrl;

}
