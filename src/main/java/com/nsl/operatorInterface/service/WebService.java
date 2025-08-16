package com.nsl.operatorInterface.service;

import com.nsl.operatorInterface.entity.UserMaster;

public interface WebService {

    UserMaster authenticateUser(String userName, String password);

}
