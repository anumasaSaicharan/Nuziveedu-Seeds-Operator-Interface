package com.nsl.operatorInterface.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nsl.operatorInterface.entity.ProductMaster;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
public class PrintOperatorInterfaceDetailsDTO {

    private Long id;
    private ProductMaster product;
    private String productName;
    private String packUnit;
    private BigDecimal packSize;
    private String gtinNumber;
    private String batchNumber;
  	private Timestamp manufactureDate;
   	private Timestamp expiryDate;
    private long printJobId;
    private String templateName;
    
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public long getPrintJobId() {
		return printJobId;
	}
	public void setPrintJobId(long printJobId) {
		this.printJobId = printJobId;
	}
	private String mfgDt;
   	private String expDt;
   	
    private BigDecimal mrp;
    private Integer qtySatchesToPrint;
   	private Timestamp modifiedOn;
    private String status;
    private String uid;
    private String serialNumber;
    private String companyCode;
    private BigDecimal unitPrice;
    
    private String useShortUrl;
    private String shortUrl;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public ProductMaster getProduct() {
		return product;
	}
	public void setProduct(ProductMaster product) {
		this.product = product;
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
	public Integer getQtySatchesToPrint() {
		return qtySatchesToPrint;
	}
	public void setQtySatchesToPrint(Integer qtySatchesToPrint) {
		this.qtySatchesToPrint = qtySatchesToPrint;
	}
	public Timestamp getModifiedOn() {
		return modifiedOn;
	}
	public void setModifiedOn(Timestamp modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getBatchNumber() {
		return batchNumber;
	}
	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getMfgDt() {
		return mfgDt;
	}
	public void setMfgDt(String mfgDt) {
		this.mfgDt = mfgDt;
	}
	public String getExpDt() {
		return expDt;
	}
	public void setExpDt(String expDt) {
		this.expDt = expDt;
	}
	public String getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
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
	
	
    
}
