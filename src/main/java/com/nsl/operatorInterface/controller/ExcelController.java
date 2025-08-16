package com.nsl.operatorInterface.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.nsl.operatorInterface.service.ExcelService;

@RestController
@RequestMapping("/rest/nsl/operatorInterface/excel/")
public class ExcelController {

	@Autowired private ExcelService excelService;

	@PostMapping("upload")
	public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
		try {
			excelService.uploadFile(file);
			return ResponseEntity.ok("File uploaded successfully!");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Error: " + e.getMessage());
		}
	}
}
