package com.nsl.operatorInterface.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nsl.operatorInterface.dto.ApiResponse;
import com.nsl.operatorInterface.exception.exceptionHandler.InvalidRequestException;
import com.nsl.operatorInterface.service.ExcelService;
import com.nsl.operatorInterface.service.RequestResponseLogService;

@RestController
@RequestMapping("/rest/nsl/operatorInterface/excel/")
public class ExcelController {

	@Autowired private ExcelService excelService;
	@Autowired private RequestResponseLogService requestResponseLogService;

	@PostMapping("upload-sap-details")
	public ResponseEntity<ApiResponse> upload(@RequestParam("file") MultipartFile file) throws Exception {
		if (file.isEmpty()) {
			throw new InvalidRequestException("Uploaded file is empty");
		}
		excelService.uploadFile(file);
		requestResponseLogService.saveRequestResponse("Upload Excel", "multipart", null, LocalDateTime.now(),LocalDateTime.now(), null);
		ApiResponse response = new ApiResponse(HttpStatus.OK.value(), "File uploaded successfully",file.getOriginalFilename());
		return ResponseEntity.ok(response);
	}

}
