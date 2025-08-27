package com.nsl.operatorInterface.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nsl.operatorInterface.entity.PrinterMaster;

public interface PrinterMasterRepository extends JpaRepository<PrinterMaster, Long> {

	List<PrinterMaster> findByActiveTrue();

	PrinterMaster findByLineNumber(String lineNumber);

	PrinterMaster findByLineNumberAndActiveTrue(String printerIp);

}
