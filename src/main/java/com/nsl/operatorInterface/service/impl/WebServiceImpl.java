package com.nsl.operatorInterface.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nsl.operatorInterface.entity.UserMaster;
import com.nsl.operatorInterface.repository.UserMasterRepository;
import com.nsl.operatorInterface.service.WebService;

@Service
public class WebServiceImpl implements WebService {

	@Autowired private UserMasterRepository userMasterRepository;

	@Override
	public UserMaster authenticateUser(String userName, String password) {
		return userMasterRepository.findByUserNameAndPasswordAndActiveTrue(userName, password).orElse(null);
	}
}
