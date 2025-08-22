package com.nsl.operatorInterface.service.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class QRCodeService {

	@SuppressWarnings({ "rawtypes", "deprecation", "unchecked" })
	public String createQRCode(String qrCodeData, String filePath) {
		try {
			String charset = "UTF-8";
			Map hintMap = new HashMap();
			int qrCodeheight = 200;
			int qrCodewidth = 200;
			BitMatrix matrix = new MultiFormatWriter().encode(new String(qrCodeData.getBytes(charset), charset),
					BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight, hintMap);
			MatrixToImageWriter.writeToFile(matrix, filePath.substring(filePath.lastIndexOf('.') + 1),
					new File(filePath));
		} catch (Exception e) {
			log.info("" + e.getStackTrace(), e);
		}
		return filePath;
	}
}