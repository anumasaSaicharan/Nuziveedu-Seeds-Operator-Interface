package com.nsl.operatorInterface.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nsl.operatorInterface.entity.PackingOrderDetails;

public interface PackingOrderDetailsRepository extends JpaRepository<PackingOrderDetails, Long> {

	@Query("SELECT DISTINCT u.productionOrderNo, u.variety, u.lotNo FROM PackingOrderDetails u WHERE u.active = true AND u.used = false")
	List<Object[]> findDistinctPoVarietyLotNoPairs();

}
