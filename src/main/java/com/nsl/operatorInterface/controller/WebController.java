package com.nsl.operatorInterface.controller;

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
import com.nsl.operatorInterface.service.WebService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/rest/nsl/operatorInterface/")
@RestController
public class WebController {
	
	@Autowired private WebService webService;

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

	    ApiResponse response = new ApiResponse(
	            HttpStatus.OK.value(),
	            "Production Orders fetched successfully",
	            poList
	    );
	    return ResponseEntity.ok(response);
	}


}
