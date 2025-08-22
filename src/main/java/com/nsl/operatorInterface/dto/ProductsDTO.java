package com.nsl.operatorInterface.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductsDTO {
	
	private long id;
    private String productName;
    private BigDecimal packSize;
    private String packUnit;
    private String gtinNumber;
    private BigDecimal mrp;
    private String status;
    private String companyId;
    private String companyName;
    private String securityKey;
    private BigDecimal unitPrice;
    private String productDescription;
    private String useShortUrl;
    private String manufactureFor;

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

	public BigDecimal getMrp() {
		return mrp;
	}

	public void setMrp(BigDecimal mrp) {
		this.mrp = mrp;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getSecurityKey() {
		return securityKey;
	}

	public void setSecurityKey(String securityKey) {
		this.securityKey = securityKey;
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

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public String getUseShortUrl() {
		return useShortUrl;
	}

	public void setUseShortUrl(String useShortUrl) {
		this.useShortUrl = useShortUrl;
	}

	public String getManufactureFor() {
		return manufactureFor;
	}

	public void setManufactureFor(String manufactureFor) {
		this.manufactureFor = manufactureFor;
	}

	

}
