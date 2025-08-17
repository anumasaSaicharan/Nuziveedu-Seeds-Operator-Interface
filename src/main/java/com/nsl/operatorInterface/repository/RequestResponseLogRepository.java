package com.nsl.operatorInterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nsl.operatorInterface.entity.RequestResponseLog;

public interface RequestResponseLogRepository extends JpaRepository<RequestResponseLog, Long> {

}
