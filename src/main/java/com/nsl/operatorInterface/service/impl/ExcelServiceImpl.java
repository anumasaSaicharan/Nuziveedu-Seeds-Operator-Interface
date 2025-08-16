package com.nsl.operatorInterface.service.impl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.nsl.operatorInterface.entity.PackingOrderDetails;
import com.nsl.operatorInterface.repository.PackingOrderDetailsRepository;
import com.nsl.operatorInterface.service.ExcelService;
import com.nsl.operatorInterface.utility.ExcelSheetProcessor;
import com.nsl.operatorInterface.utility.UIDGenerator;
import com.opencsv.CSVReader;

@Service
public class ExcelServiceImpl implements ExcelService {
	
	@Autowired private PackingOrderDetailsRepository repository;

    @Override
    public void uploadFile(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();

        if (fileName == null) throw new IllegalArgumentException("Invalid file");

        if (fileName.endsWith(".csv")) {
            processCSV(file.getInputStream());
        } else if (fileName.endsWith(".xlsx")) {
            processExcel(file.getInputStream());
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
                if (!headerSkipped) { // Skip header row
                    headerSkipped = true;
                    continue;
                }
                PackingOrderDetails order = new PackingOrderDetails();
                order.setPlantCode(line[0]);
                order.setProductionOrderNo(line[1]);
                order.setLotNo(line[2]);
                order.setQty(line[3]);
                order.setIndentNo(line[4]);
                order.setSapStatus(line[5]);
                order.setUid(UIDGenerator.generateUID(line[0]));
                order.setCreatedOn(LocalDateTime.now());
                order.setModifiedOn(LocalDateTime.now());
                order.setActive(true);
                batch.add(order);

                if (batch.size() == 1000) {
                    repository.saveAll(batch);
                    batch.clear();
                }
            }
            if (!batch.isEmpty()) repository.saveAll(batch);
        }
    }

    private void processExcel(InputStream inputStream) throws Exception {
        OPCPackage pkg = OPCPackage.open(inputStream);
        XSSFReader reader = new XSSFReader(pkg);
        var iter = reader.getSheetsData();

        while (iter.hasNext()) {
            try (InputStream sheetStream = iter.next()) {
                ExcelSheetProcessor.processSheet(sheetStream, repository);
            }
        }
    }

}
