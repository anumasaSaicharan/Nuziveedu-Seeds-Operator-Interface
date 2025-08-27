package com.nsl.operatorInterface.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductionOrderWithVarietiesDTO {

	private String productionOrderNo;
	private List<String> varieties;
	private List<String> lotNos;

}
