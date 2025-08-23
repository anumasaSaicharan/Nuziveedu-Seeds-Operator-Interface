package com.nsl.operatorInterface.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.nsl.operatorInterface.entity.UniqueCodePrintedDataDetails;

public interface UniqueCodePrintedDataDetailsRepository extends JpaRepository<UniqueCodePrintedDataDetails, Long> {

	@Query("SELECT MAX(u.serialNumber) FROM UniqueCodePrintedDataDetails u")
	Long findMaxSerialNumber();

	@Query("SELECT COUNT(u) FROM UniqueCodePrintedDataDetails u WHERE u.active = true AND u.productionOrderNo = :productionOrderNo AND u.used = false AND u.variety = :variety")
	Long getUnUsedCodesCount(@Param("productionOrderNo") String productName, @Param("variety") String variety);

	@Query(value = "SELECT * FROM UNIQUE_CODE_PRINTED_DATA_DETAILS WHERE ACTIVE = 1 AND USED = 0 AND CODES_YEAR = :year AND PRODUCTION_ORDER_NO = :productionOrderNo AND VARIETY = :variety ORDER BY SERIAL_NUMBER ASC LIMIT :limit", nativeQuery = true)
	List<UniqueCodePrintedDataDetails> fetchUnusedCodes(@Param("limit") int limit, @Param("year") int year, @Param("productionOrderNo") String productName, @Param("variety") String variety);
	
	@Query(value = "SELECT COUNT(*) FROM UNIQUE_CODE_PRINTED_DATA_DETAILS WHERE PRINT_JOB_MASTER_ID = :printJobMasterId", nativeQuery = true)
	Long getCountByPrintJobMasterId(@Param("printJobMasterId") Long printJobMasterId);

	@Query("SELECT DISTINCT u.productionOrderNo, u.variety FROM UniqueCodePrintedDataDetails u WHERE u.active = true AND u.used = false")
		List<Object[]> findDistinctPoVarietyPairs();
}
