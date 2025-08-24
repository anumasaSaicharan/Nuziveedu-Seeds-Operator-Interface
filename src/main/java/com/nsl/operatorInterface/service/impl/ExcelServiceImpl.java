package com.nsl.operatorInterface.service.impl;

import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nsl.operatorInterface.dto.ApiResponse;
import com.nsl.operatorInterface.entity.PackingOrderDetails;
import com.nsl.operatorInterface.entity.PrintedCodesHistory;
import com.nsl.operatorInterface.repository.PackingOrderDetailsRepository;
import com.nsl.operatorInterface.repository.PrintedCodesHistoryRepository;
import com.nsl.operatorInterface.repository.UniqueCodePrintedDataDetailsRepository;
import com.nsl.operatorInterface.service.ExcelService;
import com.nsl.operatorInterface.utility.ExcelSheetProcessor;
import com.opencsv.CSVReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ExcelServiceImpl implements ExcelService {
    @Autowired private PackingOrderDetailsRepository repository;
    @Autowired private UniqueCodePrintedDataDetailsRepository uniqueCodePrintedDataDetailsRepository;
    @Autowired private ExcelSheetProcessor excelSheetProcessor;
    @Autowired private Environment appConfig;
    @Autowired private PrintedCodesHistoryRepository printedCodesHistoryRepository;
    
    @Value("${printedcodes.export.path}")
    private String exportDir;
    
    @Value("${sap.file.upload.path}")
    private String uploadDir;
    
    @Async
    @Override
    public void uploadFile(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        if (fileName == null) throw new IllegalArgumentException("Invalid file");

        // Get upload path from app Properties
        if (uploadDir == null || uploadDir.isEmpty()) {
            throw new IllegalStateException("Upload path not configured");
        }

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save file to folder
        Path targetFile = uploadPath.resolve(fileName);
        file.transferTo(targetFile.toFile());

        // Now use saved file for processing
        try (InputStream stream = Files.newInputStream(targetFile)) {
            if (fileName.endsWith(".csv")) {
                processCSV(stream);
            } else if (fileName.endsWith(".xlsx")) {
                excelSheetProcessor.processSheet(stream, repository);
            } else {
                throw new IllegalArgumentException("Only .csv or .xlsx supported");
            }
        }
    }


    private void processCSV(InputStream inputStream) throws Exception {
        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
            String[] line;
            boolean headerSkipped = false;
            List<PackingOrderDetails> batch = new ArrayList<>();
            while ((line = reader.readNext()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }
                PackingOrderDetails order = new PackingOrderDetails();
                
                order.setPlantCode(line[0]);
                order.setProductionOrderNo(line[1].trim());
                order.setVariety(line[2]);
                order.setLotNo(line[3]);
                order.setQty(Integer.parseInt(line[4]));
                order.setIndentNo(line[5]);
                order.setSapStatus(line[6]);
                order.setCreatedOn(LocalDateTime.now());
                order.setActive(true);
//                processSequenceNoAndUid(line[1],Integer.parseInt(line[4]),line[0],line[2]);/** Process Unique Code Details **/
                batch.add(order);

                if (batch.size() == 1000) {
                    repository.saveAll(batch);
                    batch.clear();
                }
            }
            if (!batch.isEmpty()) repository.saveAll(batch);
        }
    }

/**
 * @Transactional public void processSequenceNoAndUid(String productionOrderNo,
 *                int quantity, String plantCode, String variety) { try {
 *                List<UniqueCodePrintedDataDetails> batch = new ArrayList<>();
 *                Long lastSerial =
 *                uniqueCodePrintedDataDetailsRepository.findMaxSerialNumber();
 *                if (lastSerial == null) { lastSerial = 1000000000L - 1; //
 *                Start before first number }
 * 
 *                for (int i = 1; i <= quantity; i++) {
 *                UniqueCodePrintedDataDetails uniqueCodePrintDetails = new
 *                UniqueCodePrintedDataDetails();
 * 
 *                String uid = UIDGenerator.generateUID(plantCode);
 *                uniqueCodePrintDetails.setUidCode(uid);
 *                uniqueCodePrintDetails.setProductionOrderNo(productionOrderNo);
 *                uniqueCodePrintDetails.setVariety(variety);
 *                uniqueCodePrintDetails.setSerialNumber(lastSerial + i);
 *                uniqueCodePrintDetails.setCreatedOn(LocalDateTime.now());
 *                uniqueCodePrintDetails.setActive(true);
 *                uniqueCodePrintDetails.setCodesYear(LocalDateTime.now().getYear());
 *                batch.add(uniqueCodePrintDetails);
 * 
 *                if (batch.size() == 1000) {
 *                uniqueCodePrintedDataDetailsRepository.saveAll(batch);
 *                batch.clear(); } } if (!batch.isEmpty()) {
 *                uniqueCodePrintedDataDetailsRepository.saveAll(batch); } }
 *                catch (Exception e) { log.error("Exception in
 *                processSequenceNoAndUid: {}", e.getMessage(), e); } }
 **/

public void exportCsv() {
	try {
		LocalDate yesterday = LocalDate.now().minusDays(1);
		List<PrintedCodesHistory> records = printedCodesHistoryRepository.findByCreatedOnDate(yesterday);

		if (records.isEmpty()) {
			log.info("No PrintedCodesHistory data found for export.");
			return;
		}

		// Ensure folder exists
		Files.createDirectories(Paths.get(exportDir));

		String fileName = "PrintedCodes_" + LocalDate.now() + ".csv";
		String filePath = exportDir + fileName;

		try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
			// CSV Header
			writer.println("UID_CODE,CREATED_ON");

			// Data Rows
			for (PrintedCodesHistory record : records) {
				writer.println(record.getUidCode() + "," + record.getCreatedOn());
			}
		}
		log.info("CSV exported successfully at: " + filePath);

	} catch (Exception e) {
		e.printStackTrace();
	}
}

public ApiResponse exportCsvManually() {
    try {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<PrintedCodesHistory> records = printedCodesHistoryRepository.findByCreatedOnDate(yesterday);

        if (records.isEmpty()) {
            log.info("No PrintedCodesHistory data found for export.");
            return new ApiResponse(200, "No records found for " + yesterday, null);
        }

        // Ensure folder exists
        Files.createDirectories(Paths.get(exportDir));

        String fileName = "PrintedCodes_" + yesterday + ".csv";
        String filePath = exportDir + fileName;

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // CSV Header
            writer.println("UID_CODE,CREATED_ON");

            // Data Rows
            for (PrintedCodesHistory record : records) {
                writer.println(record.getUidCode() + "," + record.getCreatedOn());
            }
        }

        log.info("CSV exported successfully at: " + filePath);
        return new ApiResponse(200, "CSV exported successfully", filePath);

    } catch (Exception e) {
        log.error("Error exporting CSV", e);
        return new ApiResponse(500, "Failed to export CSV: " + e.getMessage(), null);
    }
}


}
