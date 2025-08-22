package com.nsl.operatorInterface.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.nsl.operatorInterface.dto.ApiResponse;
import com.nsl.operatorInterface.entity.PrinterMaster;
import com.nsl.operatorInterface.request.PrinterMasterRequest;
import com.nsl.operatorInterface.service.PrinterMasterService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/rest/nsl/operatorInterface/printers/")
public class PrinterMasterController {

    @Autowired
    private PrinterMasterService printerMasterService;

    // Add printer
    @PostMapping("add-printer")
    public ResponseEntity<ApiResponse> addPrinter(@Valid @RequestBody PrinterMasterRequest request) {
        PrinterMaster saved = printerMasterService.addPrinter(request);
        return ResponseEntity.ok(new ApiResponse(200, "Printer added successfully", saved));
    }

    // Update printer
    @PutMapping("update-printer/{id}")
    public ResponseEntity<ApiResponse> updatePrinter(@PathVariable Long id, @Valid @RequestBody PrinterMasterRequest request) {
        PrinterMaster updated = printerMasterService.updatePrinter(id, request);
        return ResponseEntity.ok(new ApiResponse(200, "Printer updated successfully", updated));
    }

    // Get all active printers
    @GetMapping("view-all-printers")
    public ResponseEntity<ApiResponse> getAllPrinters() {
        List<PrinterMaster> printers = printerMasterService.getAllActivePrinters();
        return ResponseEntity.ok(new ApiResponse(200, "Active printers fetched successfully", printers));
    }

    // Get printer by id
    @GetMapping("get-printer-by-id/{id}")
    public ResponseEntity<ApiResponse> getPrinterById(@PathVariable Long id) {
        PrinterMaster printer = printerMasterService.getPrinterById(id);
        return ResponseEntity.ok(new ApiResponse(200, "Printer fetched successfully", printer));
    }

    // Get printer by lineNumber
    @GetMapping("get-printer-by-line")
    public ResponseEntity<ApiResponse> getPrinterByLine(@RequestParam String lineNumber) {
        PrinterMaster printer = printerMasterService.getPrinterByLineNumber(lineNumber);
        return ResponseEntity.ok(new ApiResponse(200, "Printer fetched successfully", printer));
    }
}
