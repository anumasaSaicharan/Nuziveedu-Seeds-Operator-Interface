package com.nsl.operatorInterface.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nsl.operatorInterface.dto.ProductionOrderWithVarietiesDTO;
import com.nsl.operatorInterface.entity.UserMaster;
import com.nsl.operatorInterface.repository.UniqueCodePrintedDataDetailsRepository;
import com.nsl.operatorInterface.repository.UserMasterRepository;
import com.nsl.operatorInterface.service.WebService;

@Service
public class WebServiceImpl implements WebService {

	@Autowired private UserMasterRepository userMasterRepository;
	@Autowired private UniqueCodePrintedDataDetailsRepository uniqueCodePrintedDataDetailsRepository;

	@Override
	public UserMaster authenticateUser(String userName, String password) {
		return userMasterRepository.findByUserNameAndPasswordAndActiveTrue(userName, password).orElse(null);
	}
	
	@Override
	public List<ProductionOrderWithVarietiesDTO> getDistinctProductionOrders() {
	    List<Object[]> poVarietyPairs = uniqueCodePrintedDataDetailsRepository.findDistinctPoVarietyPairs();

	    // Group by productionOrderNo
	    Map<String, List<String>> grouped = poVarietyPairs.stream().collect(Collectors.groupingBy(o -> (String) o[0],Collectors.mapping(o -> (String) o[1], Collectors.toList())));

	    // Convert to DTO list
	    return grouped.entrySet().stream().map(e -> new ProductionOrderWithVarietiesDTO(e.getKey(), e.getValue())).collect(Collectors.toList());
	}
}
