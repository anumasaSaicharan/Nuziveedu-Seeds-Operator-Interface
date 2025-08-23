package com.nsl.operatorInterface.service;

import java.util.List;

import com.nsl.operatorInterface.dto.ProductionOrderWithVarietiesDTO;
import com.nsl.operatorInterface.entity.UserMaster;

public interface WebService {

    UserMaster authenticateUser(String userName, String password);

    List<ProductionOrderWithVarietiesDTO> getDistinctProductionOrders();

}
