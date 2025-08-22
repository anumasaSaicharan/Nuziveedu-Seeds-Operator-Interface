package com.nsl.operatorInterface.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductionPlanDTO {

    private Long id;
    private String companyName;
    private String plantName;
    private String lineName;
    private String year;
    private int codeCounts;
    private int excessCodeCount;
    private String status;
    private long companyId;
    private long companyCode;
    private String key;
    private int unUsedCodesCount;
    private String plantCode;
    private String lineCode;
    private String createdDate;
    private int usedCodesCount;
    private int importedCodes;
    private int totalCodesCount;

    public int getImportedCodes() {
		return importedCodes;
	}
	public void setImportedCodes(int importedCodes) {
		this.importedCodes = importedCodes;
	}
	private int noOfCodesToImport;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getPlantName() {
		return plantName;
	}
	public void setPlantName(String plantName) {
		this.plantName = plantName;
	}
	public String getLineName() {
		return lineName;
	}
	public void setLineName(String lineName) {
		this.lineName = lineName;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public int getCodeCounts() {
		return codeCounts;
	}
	public void setCodeCounts(int codeCounts) {
		this.codeCounts = codeCounts;
	}
	public int getExcessCodeCount() {
		return excessCodeCount;
	}
	public void setExcessCodeCount(int excessCodeCount) {
		this.excessCodeCount = excessCodeCount;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}
	public long getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(long companyCode) {
		this.companyCode = companyCode;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public int getUnUsedCodesCount() {
		return unUsedCodesCount;
	}
	public void setUnUsedCodesCount(int unUsedCodesCount) {
		this.unUsedCodesCount = unUsedCodesCount;
	}
	public String getPlantCode() {
		return plantCode;
	}
	public void setPlantCode(String plantCode) {
		this.plantCode = plantCode;
	}
	public String getLineCode() {
		return lineCode;
	}
	public void setLineCode(String lineCode) {
		this.lineCode = lineCode;
	}
	public int getNoOfCodesToImport() {
		return noOfCodesToImport;
	}
	public void setNoOfCodesToImport(int noOfCodesToImport) {
		this.noOfCodesToImport = noOfCodesToImport;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public int getUsedCodesCount() {
		return usedCodesCount;
	}
	public void setUsedCodesCount(int usedCodesCount) {
		this.usedCodesCount = usedCodesCount;
	}
	public int getTotalCodesCount() {
		return totalCodesCount;
	}
	public void setTotalCodesCount(int totalCodesCount) {
		this.totalCodesCount = totalCodesCount;
	}
	
	
	
    

}
