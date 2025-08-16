package com.nsl.operatorInterface.service;

import org.springframework.web.multipart.MultipartFile;

public interface ExcelService {

	void uploadFile(MultipartFile file) throws Exception;

}
