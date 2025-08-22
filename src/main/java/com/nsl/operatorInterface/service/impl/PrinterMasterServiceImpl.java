package com.nsl.operatorInterface.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nsl.operatorInterface.entity.PrinterMaster;
import com.nsl.operatorInterface.repository.PrinterMasterRepository;
import com.nsl.operatorInterface.request.PrinterMasterRequest;
import com.nsl.operatorInterface.service.PrinterMasterService;

@Service
public class PrinterMasterServiceImpl implements PrinterMasterService {

    @Autowired
    private PrinterMasterRepository printerMasterRepository;

    @Override
    public PrinterMaster addPrinter(PrinterMasterRequest request) {
        PrinterMaster printer = new PrinterMaster();
        printer.setPlantName(request.getPlantName());
        printer.setLineName(request.getLineName());
        printer.setPrinterIp(request.getPrinterIp());
        printer.setPrinterPort(request.getPrinterPort());
        printer.setPrinterName(request.getPrinterName());
        printer.setAvailable(request.isAvailable());
        printer.setActive(request.isActive());
        printer.setPlantNumber(request.getPlantNumber());
        printer.setLineNumber(request.getLineNumber());
        printer.setCreatedOn(LocalDateTime.now());
        return printerMasterRepository.save(printer);
    }

    @Override
    public PrinterMaster updatePrinter(Long id, PrinterMasterRequest request) {
        PrinterMaster printer = printerMasterRepository.findById(id).orElseThrow(() -> new RuntimeException("Printer not found with id: " + id));

        printer.setPlantName(request.getPlantName());
        printer.setLineName(request.getLineName());
        printer.setPrinterIp(request.getPrinterIp());
        printer.setPrinterPort(request.getPrinterPort());
        printer.setPrinterName(request.getPrinterName());
        printer.setAvailable(request.isAvailable());
        printer.setActive(request.isActive());
        printer.setPlantNumber(request.getPlantNumber());
        printer.setLineNumber(request.getLineNumber());
        printer.setModifiedOn(LocalDateTime.now());

        return printerMasterRepository.save(printer);
    }

    @Override
    public List<PrinterMaster> getAllActivePrinters() {
        return printerMasterRepository.findByActiveTrue();
    }

    @Override
    public PrinterMaster getPrinterById(Long id) {
        return printerMasterRepository.findById(id).orElseThrow(() -> new RuntimeException("Printer not found with id: " + id));
    }

    @Override
    public PrinterMaster getPrinterByLineNumber(String lineNumber) {
        return printerMasterRepository.findByLineNumberAndActiveTrue(lineNumber);
    }

    @Override
    public PrinterMaster deactivatePrinter(Long id) {
        PrinterMaster printer = printerMasterRepository.findById(id).orElseThrow(() -> new RuntimeException("Printer not found with id: " + id));
        printer.setActive(false);
        printer.setModifiedOn(LocalDateTime.now());
        return printerMasterRepository.save(printer);
    }
}
