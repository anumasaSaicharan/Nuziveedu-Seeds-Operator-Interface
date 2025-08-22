package com.nsl.operatorInterface.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nsl.operatorInterface.entity.PrintJobMaster;
import com.nsl.operatorInterface.entity.ProductMaster;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PrintDataDetailsDTO {

    private PrintJobMaster printJobMaster;
	private ProductMaster productMaster;
    private String productName;
    private BigDecimal packSize;
    private String packUnit;
    private String gtinNumber;
    private String batchNumber;
  	private String manufactureDate;
  	private String expiryDate;
  	private BigDecimal mrp;
    private String uidCode;
    private String status;
    private Long serialNo;
    private String Url;
    private long printJobMasterId;
    private String companyName;
    private String printed;
    private String rejected;
    private String accepted;
    private String isSync;
    private String notSync;
    private int noOfSachets;
    private int balanceToPrint;
    private long companyId;
    private String companyCode;
    private String templateName;
    private String createdDate;    
    private String useShortUrl;
    private BigDecimal unitPrice;
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public String getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	public long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}
	public int getBalanceToPrint() {
		return balanceToPrint;
	}
	public void setBalanceToPrint(int balanceToPrint) {
		this.balanceToPrint = balanceToPrint;
	}
	public PrintJobMaster getPrintJobMaster() {
		return printJobMaster;
	}
	public void setPrintJobMaster(PrintJobMaster printJobMaster) {
		this.printJobMaster = printJobMaster;
	}
	public ProductMaster getProductMaster() {
		return productMaster;
	}
	public void setProductMaster(ProductMaster productMaster) {
		this.productMaster = productMaster;
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
	
	public String getManufactureDate() {
		return manufactureDate;
	}
	public void setManufactureDate(String manufactureDate) {
		this.manufactureDate = manufactureDate;
	}
	public String getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(String expiryDate) {
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
	
	public long getPrintJobMasterId() {
		return printJobMasterId;
	}
	public void setPrintJobMasterId(long printJobMasterId) {
		this.printJobMasterId = printJobMasterId;
	}
	
	
	public String getPrinted() {
		return printed;
	}
	public void setPrinted(String printed) {
		this.printed = printed;
	}
	public String getRejected() {
		return rejected;
	}
	public void setRejected(String rejected) {
		this.rejected = rejected;
	}
	public String getAccepted() {
		return accepted;
	}
	public void setAccepted(String accepted) {
		this.accepted = accepted;
	}
	public String getIsSync() {
		return isSync;
	}
	public void setIsSync(String isSync) {
		this.isSync = isSync;
	}
	public String getNotSync() {
		return notSync;
	}
	public void setNotSync(String notSync) {
		this.notSync = notSync;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public int getNoOfSachets() {
		return noOfSachets;
	}
	public void setNoOfSachets(int noOfSachets) {
		this.noOfSachets = noOfSachets;
	}
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}
	public BigDecimal getPackSize() {
		return packSize;
	}
	public void setPackSize(BigDecimal packSize) {
		this.packSize = packSize;
	}
	public String getUseShortUrl() {
		return useShortUrl;
	}
	public void setUseShortUrl(String useShortUrl) {
		this.useShortUrl = useShortUrl;
	}
	
	

}
