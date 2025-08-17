package com.nsl.operatorInterface.service.impl;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import com.nsl.operatorInterface.dto.ApiResponse;
import com.nsl.operatorInterface.entity.PrintJobMaster;
import com.nsl.operatorInterface.entity.ProductMaster;
import com.nsl.operatorInterface.repository.PrintJobMasterRepository;
import com.nsl.operatorInterface.repository.ProductMasterRepository;
import com.nsl.operatorInterface.repository.UniqueCodePrintedDataDetailsRepository;
import com.nsl.operatorInterface.request.PrintCodesRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PrintOperatorService {

	@Autowired private Environment appConfig;
	@Autowired private UniqueCodePrintedDataDetailsRepository uniqueCodePrintedDataDetailsRepository;
	@Autowired private ProductMasterRepository productMasterRepository;
	@Autowired private PrintJobMasterRepository printJobMasterRepository;

	
	public ApiResponse saveAndPrintCodeDetails(HttpServletRequest request, @Valid PrintCodesRequest jsonData) {
		log.info(">> savePrintCodeDetails :: SERVER_TYPE => {}", appConfig.getProperty("SERVER_TYPE"));
		if ("LIVE".equalsIgnoreCase(appConfig.getProperty("SERVER_TYPE"))) {
			return saveAndPrintCodeDetailsForProd(request, jsonData);
		} else {
//			return savePrintCodeDetailsForUAT(request, jsonData);
			return new ApiResponse();
		}
	}

	@SuppressWarnings("unchecked")
	public ApiResponse saveAndPrintCodeDetailsForProd(HttpServletRequest request, @Valid PrintCodesRequest printDto) {
	    try {
	        synchronized (this) {
	            log.info("SYNCHRONIZED_BLOCK {}", printDto);

	            int year = Calendar.getInstance().get(Calendar.YEAR);
	            Long unusedCodes = uniqueCodePrintedDataDetailsRepository.getUnUsedCodesCount(
	                    printDto.getProductName(),
	                    printDto.getCrop(),
	                    printDto.getVariety()
	            );

	            log.info("Unused Codes Available: {}", unusedCodes);

	            // Inventory validation
	            if (unusedCodes != null && unusedCodes > 0 && printDto.getQtySatchesToPrint() > unusedCodes) {
	                String msg = "Inventory Exceeded. Available Codes: " + unusedCodes;
	                return new ApiResponse(400, msg, null);
	            }

	            // Fetch or create Print Job
	            PrintJobMaster print;
	            if (printDto.getPrintJobId() == 0) {
	                print = new PrintJobMaster();
	                print.setActive(true);
	                print.setCreatedOn(LocalDateTime.now());
	                print.setQtySatchesToPrint(printDto.getQtySatchesToPrint());
	                print.setNoOfSachesPrinted(0);
	                print.setSyncQty(0);
	            } else {
	            	 print = printJobMasterRepository.findById(printDto.getPrintJobId()).orElseThrow(() -> new RuntimeException("Print Job not found with id: " + printDto.getPrintJobId()));
	            }

	            ProductMaster product = productMasterRepository.findByProductName(printDto.getProductName());

	            print.setProductName(printDto.getProductName());
	            print.setPackSize(printDto.getPackSize());
	            print.setManufactureDate(printDto.getManufactureDate());
	            print.setExpiryDate(printDto.getExpiryDate());
	            print.setBatchNumber(printDto.getBatchNumber());
	            print.setProductMaster(product);
	            print.setStartTime(LocalDateTime.now());
	            print.setEndTime(LocalDateTime.now());
	            print.setUserId(request.getHeader("userId"));
	            print.setThreadId(Thread.currentThread().getId());
//	            print.setPackUnit(printDto.getPackUnit());
//	            print.setMrp(printDto.getMrp());
//	            print.setGtinNumber(printDto.getGtinNumber());
//	            print.setCompanyCode(printDto.getCompanyCode());
//	            print.setSelectedTemplateName(printDto.getTemplateName().trim());
//	            print.setUserId(ThreadLocalData.get() != null ? ThreadLocalData.get().getId().toString() : "");
//	            print.setUnitPrice(printDto.getUnitPrice() != null ? printDto.getUnitPrice() : product.getUnitPrice());
//	            print.setUseShortUrl(printDto.getUseShortUrl());

	            // Fetch unused codes
	            String sql = "SELECT TOP " + printDto.getQtySatchesToPrint()
	                    + " * FROM UNIQUE_CODE_PRINTED_DATA_DETAILS "
	                    + "WHERE ACTIVE=1 AND USED='False' AND CODES_YEAR=" + year
	                    + " ORDER BY SERIAL_NUMBER ASC";

	            List<PrintedDataDetails> codesList = printedDataDetailsManager.findByNativeSql(sql);

	            if (codesList == null || codesList.isEmpty() || codesList.size() < printDto.getQtySatchesToPrint()) {
	                return new ApiResponse(404, "Not enough unused codes available for printing.", null);
	            }

	            printJobMasterManager.saveOrUpdate(print);

	            // Connect to printer
	            boolean printerReady = false;
	            while (true) {
	                if (socket != null && socket.isConnected()) {
	                    log.info("Socket connected, checking printer status...");
	                    while (!printerReady) {
	                        printerReady = getNetWorkPrinterStatus(socket, print.getSelectedTemplateName());
	                    }
	                    break;
	                }
	                socket = conectToNetWorkPrinter();
	                Thread.sleep(2000);
	                if (socket != null && socket.isConnected()) {
	                    log.info("Fresh Socket Connection Established");
	                    printerReady = true;
	                    break;
	                }
	            }

	            if (!printerReady || socket == null) {
	                return new ApiResponse(500, "Unable to connect to printer.", null);
	            }

	            // Start printing thread
	            List<PrintedDataDetails> emptyList = new ArrayList<>();
	            PrintedCodesHistory codesHistory = new PrintedCodesHistory();
	            DuplicatePrintCodes duplicateCodes = new DuplicatePrintCodes();
	            PrintedCodes pc = new PrintedCodes();

	            startThreadBasedOnPrinterType(socket, print, emptyList, year, codesList, codesHistory, duplicateCodes, pc);

	            Object printedCount = printedDataDetailsManager.getCountByNativeSQL("SELECT COUNT(*) FROM UNIQUE_CODE_PRINTED_DATA_DETAILS WHERE PRINT_JOB_MASTER_ID='" + print.getId() + "'");

	            Map<String, Object> jsonResponse = new HashMap<>();
	            jsonResponse.put("id", print.getId());
	            jsonResponse.put("printedCount", printedCount);

	            return new ApiResponse(200, "Print job started successfully.", jsonResponse);
	        }

	    } catch (Exception e) {
	        log.error("Error in saveAndPrintCodeDetailsForProd", e);
	        return new ApiResponse(500, "Internal server error.", null);
	    }
	}

	
	
//	  @SuppressWarnings("unchecked")
//	   	public ResponseDTO savePrintCodeDetailsForUAT(HttpServletRequest request, String jsonData)
//	   	{
//	   		ResponseDTO resp = new ResponseDTO();
//	   		try
//	   		{
//	   			synchronized (this) 
//				{
//					log.info("=UAT=SYNCHRONIZED_BLOCK=="+jsonData);	
//		   			PrintOperatorInterfaceDetailsDTO printDto = new ObjectMapper().readValue(jsonData, PrintOperatorInterfaceDetailsDTO.class);
//		   			int year = Calendar.getInstance().get(Calendar.YEAR);
//		   			Long unUsedCodes = printedDataDetailsManager.getUnUsedCodesCount();
//		   			int minmumInv = productionPlanManagers.getMinimumInventory(printDto.getCompanyCode());
//		   			//Long codes = unUsedCodes-minmumInv;
//		   			Long codes = unUsedCodes;
//		   			log.info("UAT_minmumInv==>"+minmumInv);
//		   			log.info("UAT_codes==>"+codes);
//		   			if(codes>0)
//		   			{
//		   				if(printDto.getQtySatchesToPrint()>codes) {
//		   					resp.setResponse(appConfig.getProperty("MINIMUM_INVENTRY_EXCEED"));
//		   					resp.setMessage("Minimum Inventory "+minmumInv+". Available Codes "+unUsedCodes);
//		   					resp.setStatusCode(appConfig.getProperty("ERROR_CODE"));
//		   					return resp;
//		   				}
//		   			}
//		   			CompanyDetails comp = companyDetailsManager.getCompanyDetailsByCompanyId(printDto.getCompanyCode());
//		   			if(comp==null) {
//		   				resp.setResponse(appConfig.getProperty("ERROR_MESSAGE"));
//		   				resp.setMessage(appConfig.getProperty("ERROR_MESSAGE"));
//		   				resp.setStatusCode(appConfig.getProperty("ERROR_CODE"));
//		   				return resp;
//		   			}
//		   			
//		   			PrintJobMaster print = null;
//		   			if(printDto.getPrintJobId()==0) {
//		   			 print = new PrintJobMaster();
//		   			print.setActive(true);
//		   			print.setCreatedon(DateUtils.getCurrentSystemTimestamp());
//		   			print.setQtySatchesToPrint(printDto.getQtySatchesToPrint());
//		   			print.setNoOfSachesPrinted(0);
//		   			print.setSyncQty(0);
//		   			}
//		   			else {
//		   				print = printJobMasterManager.get(printDto.getPrintJobId());
//		   			}
//		   			print.setProductName(printDto.getProductName());
//		   			print.setPackSize(printDto.getPackSize());
//		   			print.setPackUnit(printDto.getPackUnit());
//		   			print.setManufactureDate(printDto.getManufactureDate());
//		   			print.setExpiryDate(printDto.getExpiryDate());
//		   			print.setMrp(printDto.getMrp());
//		   			print.setProductMaster(printDto.getProduct());
//		   			print.setGtinNumber(printDto.getGtinNumber());
//		   			print.setBatchNumber(printDto.getBatchNumber());
//		   			print.setStartTime(DateUtils.getCurrentSystemTimestamp());
//		   			print.setEndTime(DateUtils.getCurrentSystemTimestamp());
//		   			print.setProductMaster(productManager.getProductsByNameAndSize(printDto.getProductName(),printDto.getPackSize()+""));
//		   			print.setCompanyCode(printDto.getCompanyCode());
//		   			print.setSelectedTemplateName(printDto.getTemplateName().trim());
//		   			print.setUserId(ThreadLocalData.get()!=null?ThreadLocalData.get().getId().toString():"");
//		   			print.setThreadId(Thread.currentThread().getId());
//		   			print.setStatus("UAT");
//		   			print.setUnitPrice(printDto.getUnitPrice());
//		   			print.setUseShortUrl(printDto.getUseShortUrl());
//		   			log.info("=UAT=printDto.getPrintJobId()==>"+printDto.getPrintJobId());
//		   			String sql=" select top "+printDto.getQtySatchesToPrint()+" * from UNIQUE_CODE_PRINTED_DATA_DETAILS where ACTIVE=1 and USED='False' and CODES_YEAR="+year+" order by SERIAL_NUMBER asc  ";
//		   			log.info("=UAT=Z==>"+sql);
//		   			List<PrintedDataDetails> codesList = printedDataDetailsManager.findByNativeSql(sql);
//		   			
//		   			if(codesList!=null && codesList.size()>0) {
//		   				if(printDto.getQtySatchesToPrint()<=codesList.size())
//		   					printJobMasterManager.saveOrUpdate(print);
//		   				else {
//		   					resp.setResponse(appConfig.getProperty("CODES_NOT_EXIST_MSG"));
//		   					resp.setMessage(appConfig.getProperty("CODES_NOT_EXIST_MSG"));
//		   					resp.setStatusCode(appConfig.getProperty("CODES_NOT_EXIST"));
//		   					return resp;
//		   				}
//		   			}
//		   			else {
//		   				resp.setResponse(appConfig.getProperty("CODES_NOT_EXIST_MSG"));
//		   				resp.setMessage(appConfig.getProperty("CODES_NOT_EXIST_MSG"));
//		   				resp.setStatusCode(appConfig.getProperty("CODES_NOT_EXIST"));
//		   				return resp;
//		   			}
//		   			
//		   			log.info("UAT_socket===>"+socket);
//		   			boolean prntStFlag=true;
//		   			boolean concFlag=true;
//		   			
//		   			while(!concFlag)
//		   			{
//			   			if(socket!=null && socket.isConnected())
//			   			{
//			   				concFlag= true;
//			   				log.info("UAT_socket.isConnected()==>"+socket.isConnected());
//			   				//Checking printerStatus
//			   				while(!prntStFlag) 
//			   				{
//			   					prntStFlag = getNetWorkPrinterStatus(socket,print.getSelectedTemplateName());
//			   				}
//			   			}
//					    else { 
//						 socket = conectToNetWorkPrinter();
//						 Thread.sleep(2000);
//						 if(socket!=null && socket.isConnected())
//				   			{
//							 log.info("UAT_New socket Connection Established===>");
//				   				concFlag= true;
//				   				prntStFlag=true;
//				   			}
//						 }
//		   			}
//		   			/*if(socket==null) {
//		   				resp.setResponse(appConfig.getProperty("ERROR_MESSAGE"));
//		   	   			resp.setMessage(appConfig.getProperty("ERROR_MESSAGE"));
//		   	   			resp.setStatusCode(appConfig.getProperty("ERROR_CODE"));
//		   	   			return resp;
//		   			}*/
//		   			
//		   			//The below code to loop codes using thread
//		   			List<PrintedDataDetails> emptyList = new ArrayList<>();
//		   			PrintedCodesHistory codesHistory = new PrintedCodesHistory();
//		   			DuplicatePrintCodes duplicatecodes = new DuplicatePrintCodes();
//		   			PrintedCodes pc = new PrintedCodes();
//		   			
//					log.info("UAT_prntStFlag==>"+prntStFlag);
//					if(prntStFlag) {
//						startThreadBasedForUAT(print,emptyList,year,codesList,codesHistory,duplicatecodes,pc);
//					}
//					else {
//						resp.setResponse(appConfig.getProperty("ERROR_MESSAGE"));
//		   	   			resp.setMessage(appConfig.getProperty("ERROR_MESSAGE"));
//		   	   			resp.setStatusCode(appConfig.getProperty("ERROR_CODE"));
//		   	   			return resp;
//					}
//		   			
//		   			JSONObject jsonResponse = new JSONObject();
//		   			Object Count = printedDataDetailsManager.getCountByNativeSQL("select count(*) from UNIQUE_CODE_PRINTED_DATA_DETAILS where PRINT_JOB_MASTER_ID='"+print.getId()+"'");
//		   			jsonResponse.put("id", print.getId());
//		   			jsonResponse.put("printedCount",Count);
//		   			
//		   			resp.setResponse(jsonResponse.toString());
//		   			resp.setMessage(appConfig.getProperty("SUCCESS_MESSAGE"));
//		   			resp.setStatusCode(appConfig.getProperty("SUCCESS_CODE"));
//				}
//	   		}
//	   		catch(Exception e)
//	   		{
//	   			resp.setResponse(appConfig.getProperty("ERROR_MESSAGE"));
//	   			resp.setMessage(appConfig.getProperty("ERROR_MESSAGE"));
//	   			resp.setStatusCode(appConfig.getProperty("ERROR_CODE"));
//	   			resp.setStatusCode("404");
//	   			log.info(""+e.getStackTrace(),e);
//	   		}
//	   		
//	   		return resp;
//	   	}

}
