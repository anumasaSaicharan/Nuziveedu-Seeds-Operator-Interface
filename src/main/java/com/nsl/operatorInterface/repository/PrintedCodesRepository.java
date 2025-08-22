package com.nsl.operatorInterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nsl.operatorInterface.entity.PrintedCodes;

public interface PrintedCodesRepository extends JpaRepository<PrintedCodes, Long> {

}
