package com.nsl.operatorInterface.utility;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.SAXParserFactory;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.nsl.operatorInterface.entity.PackingOrderDetails;
import com.nsl.operatorInterface.entity.UniqueCodePrintedDataDetails;
import com.nsl.operatorInterface.repository.PackingOrderDetailsRepository;
import com.nsl.operatorInterface.repository.UniqueCodePrintedDataDetailsRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ExcelSheetProcessor {

    @Autowired
    private UniqueCodePrintedDataDetailsRepository uniqueCodePrintedDataDetailsRepository;

    @SuppressWarnings("unused")
	public void processSheet(InputStream inputStream, PackingOrderDetailsRepository repository) throws Exception {
        try (OPCPackage pkg = OPCPackage.open(inputStream)) {
            XSSFReader reader = new XSSFReader(pkg);
            StylesTable styles = reader.getStylesTable();
            SharedStringsTable sst = (SharedStringsTable) reader.getSharedStringsTable();

            List<PackingOrderDetails> batch = new ArrayList<>();

            DataFormatter dataFormatter = new DataFormatter(true); // preserve raw values

            XSSFSheetXMLHandler.SheetContentsHandler handler = new XSSFSheetXMLHandler.SheetContentsHandler() {
                boolean skipHeader = false;
                String[] rowData = new String[7]; // 7 columns in Excel
                int colIndex = 0;

                @Override
                public void startRow(int rowNum) {
                    colIndex = 0;
                    rowData = new String[7]; // reset for each row
                }

                @Override
                public void endRow(int rowNum) {
                    if (!skipHeader) {
                        skipHeader = true;
                        return; // skip header row
                    }
                    try {
                        PackingOrderDetails order = new PackingOrderDetails();
                        order.setPlantCode(getValue(rowData, 0));
                        order.setProductionOrderNo(getValue(rowData, 1));
                        order.setVariety(getValue(rowData, 2));
                        order.setLotNo(getValue(rowData, 3));
                        order.setQty(parseIntSafe(getValue(rowData, 4)));
                        order.setIndentNo(getValue(rowData, 5));
                        order.setSapStatus(getValue(rowData, 6));
                        order.setCreatedOn(LocalDateTime.now());
                        order.setActive(true);

                        // Generate UIDs
                        processSequenceNoAndUid(order.getProductionOrderNo(), order.getQty(),
                                order.getPlantCode(), order.getVariety());

                        batch.add(order);

                        if (batch.size() == 1000) {
                            repository.saveAll(batch);
                            batch.clear();
                        }
                    } catch (Exception e) {
                        log.error("Error processing row {}: {}", rowNum, e.getMessage(), e);
                    }
                }

                @Override
                public void cell(String cellReference, String formattedValue, XSSFComment comment) {
                    if (colIndex < rowData.length) {
                        if (formattedValue != null) {
                            // Handle scientific notation (e.g. 4.17E+11)
                            if (formattedValue.matches("^[0-9]+\\.?[0-9]*E[+-]?[0-9]+$")) {
                                try {
                                    BigDecimal bd = new BigDecimal(formattedValue);
                                    formattedValue = bd.toPlainString();
                                } catch (Exception ex) {
                                    log.warn("Could not convert scientific notation: {}", formattedValue);
                                }
                            }
                            rowData[colIndex] = formattedValue.trim();
                        } else {
                            rowData[colIndex] = "";
                        }
                    }
                    colIndex++;
                }

                @Override
                public void headerFooter(String text, boolean isHeader, String tagName) {
                    // ignored
                }
            };

            InputStream sheetInputStream = reader.getSheetsData().next();
            try (sheetInputStream) {
                SAXParserFactory saxFactory = SAXParserFactory.newInstance();
                saxFactory.setNamespaceAware(true);
                XMLReader parser = saxFactory.newSAXParser().getXMLReader();
                parser.setContentHandler(new XSSFSheetXMLHandler(styles, sst, handler, false));
                parser.parse(new InputSource(sheetInputStream));
            }

            if (!batch.isEmpty()) {
                repository.saveAll(batch);
            }
        }
    }

    private String getValue(String[] arr, int index) {
        return (index < arr.length && arr[index] != null) ? arr[index] : "";
    }

    private int parseIntSafe(String val) {
        try {
            return Integer.parseInt(val);
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public void processSequenceNoAndUid(String productionOrderNo, int quantity, String plantCode, String variety) {
        try {
            List<UniqueCodePrintedDataDetails> batch = new ArrayList<>();
            Long lastSerial = uniqueCodePrintedDataDetailsRepository.findMaxSerialNumber();
            if (lastSerial == null) {
                lastSerial = 1000000000L - 1; // Start before first number
            }

            for (int i = 1; i <= quantity; i++) {
                UniqueCodePrintedDataDetails uniqueCodePrintDetails = new UniqueCodePrintedDataDetails();

                String uid = UIDGenerator.generateUID(plantCode);
                uniqueCodePrintDetails.setUidCode(uid);
                uniqueCodePrintDetails.setProductionOrderNo(productionOrderNo);
                uniqueCodePrintDetails.setVariety(variety);
                uniqueCodePrintDetails.setSerialNumber(lastSerial + i);
                uniqueCodePrintDetails.setCreatedOn(LocalDateTime.now());
                uniqueCodePrintDetails.setActive(true);
                uniqueCodePrintDetails.setCodesYear(LocalDateTime.now().getYear());
                batch.add(uniqueCodePrintDetails);

                if (batch.size() == 1000) {
                    uniqueCodePrintedDataDetailsRepository.saveAll(batch);
                    batch.clear();
                }
            }
            if (!batch.isEmpty()) {
                uniqueCodePrintedDataDetailsRepository.saveAll(batch);
            }
        } catch (Exception e) {
            log.error("Exception in processSequenceNoAndUid: {}", e.getMessage(), e);
        }
    }
}
