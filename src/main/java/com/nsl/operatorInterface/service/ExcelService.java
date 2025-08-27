package com.nsl.operatorInterface.service;

import org.springframework.web.multipart.MultipartFile;
import com.nsl.operatorInterface.dto.ApiResponse;

public interface ExcelService {

	void uploadFile(MultipartFile file) throws Exception;

	ApiResponse getPoBasedReport(String po, String requestType);

}
