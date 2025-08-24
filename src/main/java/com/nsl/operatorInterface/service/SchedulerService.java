package com.nsl.operatorInterface.service;

import java.sql.Timestamp;
import java.time.Instant;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.nsl.operatorInterface.entity.SchedulerDetails;
import com.nsl.operatorInterface.repository.ShedulerDetailsRepository;
import com.nsl.operatorInterface.service.impl.ExcelServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final ExcelServiceImpl excelServiceImpl;
    private final ShedulerDetailsRepository schedulerDetailsRepository;

    /**
     * Scheduler - Runs daily at 2 AM
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void exportPrintedCodesToCsv() {
        log.info("Scheduler started: Exporting Printed Codes to CSV");

        // Create a scheduler log record
        SchedulerDetails schedulerDetails = new SchedulerDetails();
        schedulerDetails.setJobName("PrintedCodes CSV Export");
        schedulerDetails.setStartedAt(Timestamp.from(Instant.now()));
        schedulerDetails.setStatus("STARTED");
        schedulerDetailsRepository.save(schedulerDetails);

        try {
            excelServiceImpl.exportCsv();

            // Update record on success
            schedulerDetails.setEndedAt(Timestamp.from(Instant.now()));
            schedulerDetails.setStatus("SUCCESS");
            schedulerDetails.setDetails("CSV exported successfully");
            schedulerDetailsRepository.save(schedulerDetails);

            log.info("Scheduler completed: CSV export successful");

        } catch (Exception e) {
            // Update record on failure
            schedulerDetails.setEndedAt(Timestamp.from(Instant.now()));
            schedulerDetails.setStatus("FAILED");
            schedulerDetails.setDetails("Error: " + e.getMessage());
            schedulerDetailsRepository.save(schedulerDetails);

            log.error("Error while exporting Printed Codes CSV: {}", e.getMessage(), e);
        }
    }
}
