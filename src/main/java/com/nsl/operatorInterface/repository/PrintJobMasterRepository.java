package com.nsl.operatorInterface.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nsl.operatorInterface.entity.PrintJobMaster;

public interface PrintJobMasterRepository extends JpaRepository<PrintJobMaster, Long> {

	Optional<PrintJobMaster> findById(Long id);

}
