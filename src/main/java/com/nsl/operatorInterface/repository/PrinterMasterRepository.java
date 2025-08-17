package com.nsl.operatorInterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nsl.operatorInterface.entity.PrinterMaster;

public interface PrinterMasterRepository extends JpaRepository<PrinterMaster, Long> {

}
