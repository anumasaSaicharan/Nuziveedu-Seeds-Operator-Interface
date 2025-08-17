package com.nsl.operatorInterface.service;

import java.time.LocalDateTime;

public interface RequestResponseLogService {

	String saveRequestResponse(String api, String request, String response, LocalDateTime reqOn, LocalDateTime respOn,String status);

}
