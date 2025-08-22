package com.nsl.operatorInterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nsl.operatorInterface.entity.DuplicatePrintCodes;

public interface DuplicatePrintCodesRepository extends JpaRepository<DuplicatePrintCodes, Long> {

}
