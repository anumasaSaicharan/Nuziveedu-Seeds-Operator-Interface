package com.nsl.operatorInterface.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PrintDataDetailsSyncDTO {

	private long id;
    private String productName;
    private BigDecimal packSize;
    private String packUnit;
    private String gtinNumber;
    private String batchNumber;
  	private Timestamp manufactureDate;
  	private Timestamp expiryDate;
  	private BigDecimal mrp;
  	private BigDecimal unitPrice;
    private String uidCode;
    private String status;
    private Long serialNo;
    private String Url;
	private Timestamp createdon; 
	private Boolean active;
	private Boolean isSync;
	private Boolean unUsed;
	private int year;
	private String companyId;
	private String printedOn;
	private String  printedUserId;
	private String  printJobId;
	private String codesYear;
	private String useShortUrl;
	private String shortUrl;
	private String plantNumber;
	private String lineNumber;
	public String getPrintJobId() {
		return printJobId;
	}
	public void setPrintJobId(String printJobId) {
		this.printJobId = printJobId;
	}
	public String getPrintedOn() {
		return printedOn;
	}
	public void setPrintedOn(String printedOn) {
		this.printedOn = printedOn;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	public String getPackUnit() {
		return packUnit;
	}
	public void setPackUnit(String packUnit) {
		this.packUnit = packUnit;
	}
	public String getGtinNumber() {
		return gtinNumber;
	}
	public void setGtinNumber(String gtinNumber) {
		this.gtinNumber = gtinNumber;
	}
	public String getBatchNumber() {
		return batchNumber;
	}
	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}
	public Timestamp getManufactureDate() {
		return manufactureDate;
	}
	public void setManufactureDate(Timestamp manufactureDate) {
		this.manufactureDate = manufactureDate;
	}
	public Timestamp getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(Timestamp expiryDate) {
		this.expiryDate = expiryDate;
	}
	public BigDecimal getMrp() {
		return mrp;
	}
	public void setMrp(BigDecimal mrp) {
		this.mrp = mrp;
	}
	public String getUidCode() {
		return uidCode;
	}
	public void setUidCode(String uidCode) {
		this.uidCode = uidCode;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(Long serialNo) {
		this.serialNo = serialNo;
	}
	
	public String getUrl() {
		return Url;
	}
	public void setUrl(String url) {
		Url = url;
	}
	
	public Timestamp getCreatedon() {
		return createdon;
	}
	public void setCreatedon(Timestamp createdon) {
		this.createdon = createdon;
	}
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Boolean getIsSync() {
		return isSync;
	}
	public void setIsSync(Boolean isSync) {
		this.isSync = isSync;
	}
	public Boolean getUnUsed() {
		return unUsed;
	}
	public void setUnUsed(Boolean unUsed) {
		this.unUsed = unUsed;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getCompanyId() {
		return companyId;
	}
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	public String getCodesYear() {
		return codesYear;
	}
	public void setCodesYear(String codesYear) {
		this.codesYear = codesYear;
	}
	public BigDecimal getPackSize() {
		return packSize;
	}
	public void setPackSize(BigDecimal packSize) {
		this.packSize = packSize;
	}
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}
	public String getPrintedUserId() {
		return printedUserId;
	}
	public void setPrintedUserId(String printedUserId) {
		this.printedUserId = printedUserId;
	}
	public String getUseShortUrl() {
		return useShortUrl;
	}
	public void setUseShortUrl(String useShortUrl) {
		this.useShortUrl = useShortUrl;
	}
	public String getShortUrl() {
		return shortUrl;
	}
	public void setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
	}
	public String getPlantNumber() {
		return plantNumber;
	}
	public void setPlantNumber(String plantNumber) {
		this.plantNumber = plantNumber;
	}
	public String getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(String lineNumber) {
		this.lineNumber = lineNumber;
	}
	

	
	
	
    
    

}
