package com.nsl.operatorInterface.service.impl;

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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelServiceImpl implements ExcelService {
    @Autowired private PackingOrderDetailsRepository repository;
    @Autowired private UniqueCodePrintedDataDetailsRepository uniqueCodePrintedDataDetailsRepository;

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
                ExcelSheetProcessor.processSheet(stream, repository);
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
                
                String Uid = UIDGenerator.generateUID(line[0]);
                
                order.setPlantCode(line[0]);
                order.setProductionOrderNo(line[1]);
                order.setLotNo(line[2]);
                order.setQty(line[3]);
                order.setIndentNo(line[4]);
                order.setSapStatus(line[5]);
                order.setUid(Uid);
                order.setCreatedOn(LocalDateTime.now());
                order.setActive(true);
                processSequenceNoAndUid(line[1],Uid);/** Process Unique Code Details **/
                batch.add(order);

                if (batch.size() == 1000) {
                    repository.saveAll(batch);
                    batch.clear();
                }
            }
            if (!batch.isEmpty()) repository.saveAll(batch);
        }
    }

	public void processSequenceNoAndUid(String productionOrderNo, String uid) throws Exception {
		UniqueCodePrintedDataDetails uniqueCodePrintDetails = new UniqueCodePrintedDataDetails();
		uniqueCodePrintDetails.setUidCode(uid);
		uniqueCodePrintDetails.setProductionOrderNo(productionOrderNo);
		uniqueCodePrintDetails.setSerialNumber(generateNextSerialNumber());
		uniqueCodePrintedDataDetailsRepository.save(uniqueCodePrintDetails);
	}

	public Long generateNextSerialNumber() throws Exception {
		Long lastSerial = uniqueCodePrintedDataDetailsRepository.findMaxSerialNumber();
		if (lastSerial == null) {
			return 1L;
		}
		return lastSerial + 1;
	}
}
