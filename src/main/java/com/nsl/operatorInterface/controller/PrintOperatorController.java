package com.nsl.operatorInterface.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nsl.operatorInterface.dto.ApiResponse;
import com.nsl.operatorInterface.request.PrintCodesRequest;
import com.nsl.operatorInterface.service.RequestResponseLogService;
import com.nsl.operatorInterface.service.impl.PrintOperatorService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/rest/nsl/operatorInterface/printCodes/")
public class PrintOperatorController {

    @Autowired 
    private PrintOperatorService printOperatorService;

    @Autowired 
    private RequestResponseLogService requestResponseLogService;

    @PostMapping("save-print-code-details")
    public ResponseEntity<ApiResponse> savePrintCodeDetails(HttpServletRequest request, @RequestBody @Valid PrintCodesRequest jsonData) {
        log.info("Inside /savePrintCodeDetails");
        ApiResponse resp = printOperatorService.saveAndPrintCodeDetails(request, jsonData);
        requestResponseLogService.saveRequestResponse("/savePrintCodeDetails",jsonData.toString(),resp.getResponse() != null ? resp.getResponse().toString() : "",LocalDateTime.now(),LocalDateTime.now(),resp.getMessage());
        return new ResponseEntity<>(resp, HttpStatus.valueOf(resp.getStatusCode()));
    }
    
    @PostMapping("stop-printing")
    public ResponseEntity<ApiResponse> stopPrinting(HttpServletRequest request, @RequestBody String jsonData) {
        log.info("Inside /stopPrinting");
        ApiResponse resp = printOperatorService.stopPrinting(request, jsonData);
        requestResponseLogService.saveRequestResponse("/stopPrinting",jsonData.toString(),resp.getResponse() != null ? resp.getResponse().toString() : "",LocalDateTime.now(),LocalDateTime.now(),resp.getMessage());
        return new ResponseEntity<>(resp, HttpStatus.valueOf(resp.getStatusCode()));
    }
    
    @PostMapping("view-print-code-status")
    public ResponseEntity<ApiResponse> getPrintCodeStatus(HttpServletRequest request, @RequestBody String jsonData) {
        log.info("Inside /getPrintCodeStatus");
        ApiResponse resp = printOperatorService.getPrintCodeStatus(request, jsonData);
        requestResponseLogService.saveRequestResponse("/getPrintCodeStatus",jsonData.toString(),resp.getResponse() != null ? resp.getResponse().toString() : "",LocalDateTime.now(),LocalDateTime.now(),resp.getMessage());
        return new ResponseEntity<>(resp, HttpStatus.valueOf(resp.getStatusCode()));
    }
    
    @PostMapping("generate-unique-codes")
    public ResponseEntity<ApiResponse> generateUniqueCodes(HttpServletRequest request, @RequestBody String jsonData) {
        log.info("Inside /generateUniqueCodes");
        ApiResponse resp = printOperatorService.generateUniqueCodes(request, jsonData);
        requestResponseLogService.saveRequestResponse("/generateUniqueCodes",jsonData.toString(),resp.getResponse() != null ? resp.getResponse().toString() : "",LocalDateTime.now(),LocalDateTime.now(),resp.getMessage());
        return new ResponseEntity<>(resp, HttpStatus.valueOf(resp.getStatusCode()));
    }
    
    @GetMapping("get-used-and-unused-codes-count")
    public ResponseEntity<ApiResponse> getUsedAndUnusedCodesCount(HttpServletRequest request) {
        log.info("Inside /getUsedAndUnusedCodesCount");
        ApiResponse resp = printOperatorService.getUsedAndUnusedCodesCount(request);
        requestResponseLogService.saveRequestResponse("/getUsedAndUnusedCodesCount","",resp.getResponse() != null ? resp.getResponse().toString() : "",LocalDateTime.now(),LocalDateTime.now(),resp.getMessage());
        return new ResponseEntity<>(resp, HttpStatus.valueOf(resp.getStatusCode()));
    }
    
}
