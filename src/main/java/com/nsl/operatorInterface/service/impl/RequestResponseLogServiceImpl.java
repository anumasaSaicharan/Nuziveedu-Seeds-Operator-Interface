package com.nsl.operatorInterface.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nsl.operatorInterface.entity.RequestResponseLog;
import com.nsl.operatorInterface.repository.RequestResponseLogRepository;
import com.nsl.operatorInterface.service.RequestResponseLogService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RequestResponseLogServiceImpl implements RequestResponseLogService {

	@Autowired
	private RequestResponseLogRepository requestResponseLogRepository;

	@Override
	public String saveRequestResponse(String api, String request, String response, LocalDateTime reqOn, LocalDateTime respOn,String status) {
		try {
			log.info("saveRequestResponse=================>");
			RequestResponseLog req = new RequestResponseLog();
			req.setRequest(request);
			req.setRequestAPI(api);
			req.setRequestedOn(reqOn);
//			if (ThreadLocalData.get() != null) {
//				req.setRequestedUserId(ThreadLocalData.get().getId().toString());
//				req.setRequestedUserName(ThreadLocalData.get().getName());
//			}
			req.setResponse(response);
			req.setResponseOn(respOn);
			req.setResponseStatus(status);
			requestResponseLogRepository.save(req);
		} catch (Exception e) {
			log.info("Exception_While_Response_SAVE====saveRequestResponse====>" + e.getStackTrace(), e);
		}
		return status;
	}
}
