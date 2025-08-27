package com.nsl.operatorInterface.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nsl.operatorInterface.dto.ProductionOrderWithVarietiesDTO;
import com.nsl.operatorInterface.entity.UserMaster;
import com.nsl.operatorInterface.repository.PackingOrderDetailsRepository;
import com.nsl.operatorInterface.repository.UniqueCodePrintedDataDetailsRepository;
import com.nsl.operatorInterface.repository.UserMasterRepository;
import com.nsl.operatorInterface.service.WebService;

@Service
public class WebServiceImpl implements WebService {

	@Autowired private UserMasterRepository userMasterRepository;
	@Autowired private UniqueCodePrintedDataDetailsRepository uniqueCodePrintedDataDetailsRepository;
	@Autowired private PackingOrderDetailsRepository packingOrderDetailsRepository;
	
	@Override
	public UserMaster authenticateUser(String userName, String password) {
		return userMasterRepository.findByUserNameAndPasswordAndActiveTrue(userName, password).orElse(null);
	}
	
	@Override
	public List<ProductionOrderWithVarietiesDTO> getDistinctProductionOrders() {
	    List<Object[]> poVarietyLotPairs = packingOrderDetailsRepository.findDistinctPoVarietyLotNoPairs();
	    Map<String, Map<String, List<String>>> grouped = poVarietyLotPairs.stream().collect(Collectors.groupingBy(o -> (String) o[0],Collectors.groupingBy(o -> (String) o[1],Collectors.mapping(o -> (String) o[2], Collectors.toList()))));
	    return grouped.entrySet().stream().map(e -> new ProductionOrderWithVarietiesDTO(e.getKey(),new ArrayList<>(e.getValue().keySet()),e.getValue().values().stream().flatMap(List::stream).collect(Collectors.toList()))).collect(Collectors.toList());
	}

}
