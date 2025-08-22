package com.nsl.operatorInterface.service.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nsl.operatorInterface.entity.PackingOrderDetails;
import com.nsl.operatorInterface.entity.UniqueCodePrintedDataDetails;
import com.nsl.operatorInterface.repository.PackingOrderDetailsRepository;
import com.nsl.operatorInterface.repository.UniqueCodePrintedDataDetailsRepository;
import com.nsl.operatorInterface.service.ExcelService;
import com.nsl.operatorInterface.utility.ExcelSheetProcessor;
import com.nsl.operatorInterface.utility.UIDGenerator;
import com.opencsv.CSVReader;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ExcelServiceImpl implements ExcelService {
    @Autowired private PackingOrderDetailsRepository repository;
    @Autowired private UniqueCodePrintedDataDetailsRepository uniqueCodePrintedDataDetailsRepository;
    @Autowired private ExcelSheetProcessor excelSheetProcessor;

    @Async
    @Override
    public void uploadFile(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        if (fileName == null) throw new IllegalArgumentException("Invalid file");
        byte[] fileBytes = file.getBytes(); // buffer for safe thread use

        if (fileName.endsWith(".csv")) {
            try (InputStream stream = new ByteArrayInputStream(fileBytes)) {
                processCSV(stream);
            }
        } else if (fileName.endsWith(".xlsx")) {
            try (InputStream stream = new ByteArrayInputStream(fileBytes)) {
                excelSheetProcessor.processSheet(stream, repository);
            }
        } else {
            throw new IllegalArgumentException("Only .csv or .xlsx supported");
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
                processSequenceNoAndUid(line[1],Integer.parseInt(line[4]),line[0],line[2]);/** Process Unique Code Details **/
                batch.add(order);

                if (batch.size() == 1000) {
                    repository.saveAll(batch);
                    batch.clear();
                }
            }
            if (!batch.isEmpty()) repository.saveAll(batch);
        }
    }

    @Transactional
    public void processSequenceNoAndUid(String productionOrderNo, int quantity, String plantCode, String variety) throws Exception {
		try {
    	Long lastSerial = uniqueCodePrintedDataDetailsRepository.findMaxSerialNumber();
        if (lastSerial == null) {
            lastSerial = 1000000000L - 1; // Start before first number
        }

        List<UniqueCodePrintedDataDetails> batch = new ArrayList<>();

        for (int i = 1; i <= quantity; i++) {
            UniqueCodePrintedDataDetails uniqueCodePrintDetails = new UniqueCodePrintedDataDetails();

            String uid = UIDGenerator.generateUID(plantCode);
            uniqueCodePrintDetails.setUidCode(uid);
            uniqueCodePrintDetails.setProductionOrderNo(productionOrderNo);
            uniqueCodePrintDetails.setSerialNumber(lastSerial + i);
            uniqueCodePrintDetails.setVariety(variety);
            uniqueCodePrintDetails.setCreatedOn(LocalDateTime.now());
            uniqueCodePrintDetails.setActive(true);
            uniqueCodePrintDetails.setCodesYear(LocalDateTime.now().getYear());
            batch.add(uniqueCodePrintDetails);

            // Save in chunks of 1000
            if (batch.size() == 1000) {
                uniqueCodePrintedDataDetailsRepository.saveAll(batch);
				batch.clear();
			}
		}
		// Save any remaining records
		if (!batch.isEmpty()) {
			uniqueCodePrintedDataDetailsRepository.saveAll(batch);
		}
	} catch (Exception e) {
		log.info("Exception_in_processSequenceNoAndUid_in_ExcelServiceImpl===>" + e);
	}
}
}
