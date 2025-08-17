package com.nsl.operatorInterface.utility;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.nsl.operatorInterface.entity.PackingOrderDetails;
import com.nsl.operatorInterface.repository.PackingOrderDetailsRepository;

import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ExcelSheetProcessor {
    public static void processSheet(InputStream inputStream, PackingOrderDetailsRepository repository) throws Exception {
        try (OPCPackage pkg = OPCPackage.open(inputStream)) {
            XSSFReader reader = new XSSFReader(pkg);
            StylesTable styles = reader.getStylesTable();
            SharedStringsTable sst = (SharedStringsTable) reader.getSharedStringsTable();

            List<PackingOrderDetails> batch = new ArrayList<>();

            XSSFSheetXMLHandler.SheetContentsHandler handler = new XSSFSheetXMLHandler.SheetContentsHandler() {
                boolean skipHeader = false;
                String[] rowData = new String[6];
                int colIndex = 0;

                @Override
                public void startRow(int rowNum) {
                    colIndex = 0;
                }

                @Override
                public void endRow(int rowNum) {
                    if (!skipHeader) {
                        skipHeader = true;
                        return;
                    }
                    PackingOrderDetails order = new PackingOrderDetails();
                    order.setPlantCode(rowData[0]);
                    order.setProductionOrderNo(rowData[1]);
                    order.setLotNo(rowData[2]);
                    order.setQty(rowData[3]);
                    order.setIndentNo(rowData[4]);
                    order.setSapStatus(rowData[5]);
                    order.setUid(UIDGenerator.generateUID(rowData[0]));
                    order.setCreatedOn(LocalDateTime.now());
                    order.setModifiedOn(LocalDateTime.now());
                    order.setActive(true);
                    batch.add(order);

                    if (batch.size() == 1000) {
                        repository.saveAll(batch);
                        batch.clear();
                    }
                }

                @Override
                public void cell(String cellReference, String formattedValue, XSSFComment comment) {
                    if (colIndex < rowData.length) {
                        rowData[colIndex] = formattedValue != null ? formattedValue.trim() : "";
                    }
                    colIndex++;
                }

                @Override
                public void headerFooter(String text, boolean isHeader, String tagName) {/*ignored*/}
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
}
