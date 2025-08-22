package com.nsl.operatorInterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nsl.operatorInterface.entity.PrintedCodesHistory;

public interface PrintedCodesHistoryRepository extends JpaRepository<PrintedCodesHistory, Long> {

}
