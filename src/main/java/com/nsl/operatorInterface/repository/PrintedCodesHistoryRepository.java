package com.nsl.operatorInterface.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.nsl.operatorInterface.entity.PrintedCodesHistory;

public interface PrintedCodesHistoryRepository extends JpaRepository<PrintedCodesHistory, Long> {

	@Query("SELECT p FROM PrintedCodesHistory p WHERE DATE(p.createdOn) = :date")
	List<PrintedCodesHistory> findByCreatedOnDate(@Param("date") LocalDate date);

}
