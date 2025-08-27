package com.nsl.operatorInterface.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nsl.operatorInterface.dto.ApiResponse;
import com.nsl.operatorInterface.dto.ProductionOrderWithVarietiesDTO;
import com.nsl.operatorInterface.entity.UserMaster;
import com.nsl.operatorInterface.exception.exceptionHandler.ResourceNotFoundException;
import com.nsl.operatorInterface.exception.exceptionHandler.UserNotFoundException;
import com.nsl.operatorInterface.service.RequestResponseLogService;
import com.nsl.operatorInterface.service.TemplateMasterService;
import com.nsl.operatorInterface.service.WebService;
import com.nsl.operatorInterface.service.impl.ExcelServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/rest/nsl/operatorInterface/")
@RestController
public class WebController {
	
	@Autowired private WebService webService;
	@Autowired private ExcelServiceImpl excelServiceImpl;
    @Autowired private RequestResponseLogService requestResponseLogService;

	@GetMapping("authenticate")
    public ResponseEntity<ApiResponse> authenticateUser(@RequestParam String userName,@RequestParam String password) {
        UserMaster user = webService.authenticateUser(userName, password);
        if (user == null) {
            throw new UserNotFoundException("User not found or inactive");
        }
        ApiResponse response = new ApiResponse(HttpStatus.OK.value(), "user Loggedin successfully", user);
        return ResponseEntity.ok(response);
    }
	
	@GetMapping("get-distinct-production-orders")
	public ResponseEntity<ApiResponse> getDistinctProductionOrders() {
	    List<ProductionOrderWithVarietiesDTO> poList = webService.getDistinctProductionOrders();
	    if (poList == null || poList.isEmpty()) {
	        throw new ResourceNotFoundException("No Production Order's Found.");
	    }
	    ApiResponse response = new ApiResponse(HttpStatus.OK.value(),"Production Orders fetched successfully",poList);
	    return ResponseEntity.ok(response);
	}
	
	@GetMapping("send-printed-codes-excel-report")
	public ResponseEntity<ApiResponse> sendPrintedCodesExcelReport() {
	    log.info("Inside /send-printed-codes-excel-report");
	    LocalDateTime requestTime = LocalDateTime.now();
	    ApiResponse resp;
	    try {
	        resp = excelServiceImpl.exportCsvManually();
	    } catch (Exception e) {
	        log.error("Error while exporting Printed Codes Excel Report", e);
	        resp = new ApiResponse(500, "Failed to generate Excel report", null);
	    }
	    LocalDateTime responseTime = LocalDateTime.now();
	    requestResponseLogService.saveRequestResponse("/send-printed-codes-excel-report","",resp.getResponse() != null ? resp.getResponse().toString() : "",requestTime,responseTime,resp.getMessage());
	    return new ResponseEntity<>(resp, HttpStatus.valueOf(resp.getStatusCode()));
	}

}
