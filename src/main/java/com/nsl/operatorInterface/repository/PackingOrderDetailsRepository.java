package com.nsl.operatorInterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nsl.operatorInterface.entity.PackingOrderDetails;

public interface PackingOrderDetailsRepository extends JpaRepository<PackingOrderDetails, Long> {

}
