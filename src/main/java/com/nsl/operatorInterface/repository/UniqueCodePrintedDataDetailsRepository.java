package com.nsl.operatorInterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nsl.operatorInterface.entity.UniqueCodePrintedDataDetails;

public interface UniqueCodePrintedDataDetailsRepository extends JpaRepository<UniqueCodePrintedDataDetails, Long> {

	@Query("SELECT MAX(u.serialNumber) FROM UniqueCodePrintedDataDetails u")
	Long findMaxSerialNumber();

	@Query("SELECT COUNT(u) FROM UniqueCodePrintedDataDetails u WHERE u.active = true AND u.used = false AND u.productName = :productName AND u.cropName = :cropName AND u.variety = :variety")
	Long getUnUsedCodesCount(@Param("productName") String productName, @Param("cropName") String cropName, @Param("variety") String variety);
}
