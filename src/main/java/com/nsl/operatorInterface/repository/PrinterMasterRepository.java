package com.nsl.operatorInterface.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nsl.operatorInterface.entity.PrinterMaster;

public interface PrinterMasterRepository extends JpaRepository<PrinterMaster, Long> {

	List<PrinterMaster> findByActiveTrue();
	
    // Custom finder
    PrinterMaster findByLineNumber(String lineNumber);

    // OR if you also want to check only active printers
    PrinterMaster findByLineNumberAndActiveTrue(String lineNumber);

}
