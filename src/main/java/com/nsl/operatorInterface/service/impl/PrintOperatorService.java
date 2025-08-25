package com.nsl.operatorInterface.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.nsl.operatorInterface.dto.ApiResponse;
import com.nsl.operatorInterface.entity.CompanyDetails;
import com.nsl.operatorInterface.entity.DuplicatePrintCodes;
import com.nsl.operatorInterface.entity.PrintJobMaster;
import com.nsl.operatorInterface.entity.PrintedCodes;
import com.nsl.operatorInterface.entity.PrintedCodesHistory;
import com.nsl.operatorInterface.entity.PrinterMaster;
import com.nsl.operatorInterface.entity.ProductMaster;
import com.nsl.operatorInterface.entity.UniqueCodePrintedDataDetails;
import com.nsl.operatorInterface.repository.CompanyDetailsRepository;
import com.nsl.operatorInterface.repository.DuplicatePrintCodesRepository;
import com.nsl.operatorInterface.repository.PrintJobMasterRepository;
import com.nsl.operatorInterface.repository.PrintedCodesHistoryRepository;
import com.nsl.operatorInterface.repository.PrintedCodesRepository;
import com.nsl.operatorInterface.repository.PrinterMasterRepository;
import com.nsl.operatorInterface.repository.ProductMasterRepository;
import com.nsl.operatorInterface.repository.UniqueCodePrintedDataDetailsRepository;
import com.nsl.operatorInterface.request.PrintCodesRequest;
import com.nsl.operatorInterface.threadService.PrintThreadServiceDominoPrinter;
import com.nsl.operatorInterface.threadService.PrintThreadServiceDominoPrinterVx;
import com.nsl.operatorInterface.threadService.PrintThreadServiceWorkingForUAT;
import com.nsl.operatorInterface.utility.HibernateDao;
import com.nsl.operatorInterface.utility.UIDGenerator;

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
	@Autowired private CompanyDetailsRepository companyDetailsRepository;
	@Autowired private PrinterMasterRepository printerMasterRepository;
	@Autowired private PrintedCodesRepository printedCodesRepository;
	@Autowired private PrintedCodesHistoryRepository printedCodesHistoryRepository;
	@Autowired private DuplicatePrintCodesRepository duplicatePrintCodesRepository;
	
    private static Socket socket;
	private Map<String, Socket> lineSocketMap = new ConcurrentHashMap<>();
	@Autowired
	@Qualifier("hibernateDao")
	protected HibernateDao hibernateDao;
	private static boolean forLoopFlag =true;
	
    @Value("${plant.code}")
    private String plantCode;
    
    @Value("${line.code}")
    private String lineNumber;

	public ApiResponse saveAndPrintCodeDetails(HttpServletRequest request, @Valid PrintCodesRequest jsonData) {
		log.info(">> savePrintCodeDetails :: SERVER_TYPE => {}", appConfig.getProperty("SERVER_TYPE"));
		if ("LIVE".equalsIgnoreCase(appConfig.getProperty("SERVER_TYPE"))) {
			return saveAndPrintCodeDetailsForProd(request, jsonData);
		} else {
			return savePrintCodeDetails_UAT(request, jsonData);
		}
	}

	@SuppressWarnings("unchecked")
	public ApiResponse saveAndPrintCodeDetailsForProd(HttpServletRequest request, @Valid PrintCodesRequest printDto) {
		ApiResponse resp = new ApiResponse();
		try {
			synchronized (this) {
				log.info("PROD=======>SYNCHRONIZED_BLOCK {}", printDto);

				int year = Calendar.getInstance().get(Calendar.YEAR);
//				Long unusedCodes = uniqueCodePrintedDataDetailsRepository.getUnUsedCodesCount(printDto.getProductName(),printDto.getCrop(), printDto.getVariety());
				Long unusedCodes = uniqueCodePrintedDataDetailsRepository.getUnUsedCodesCount(printDto.getProductionOrderNo(),printDto.getVariety());

				log.info("Unused Codes Available: {}", unusedCodes);

				// Inventory validation
				if (unusedCodes != null && unusedCodes > 0 && printDto.getQtySatchesToPrint() > unusedCodes) {
					String msg = "Inventory Exceeded. Available Codes: " + unusedCodes;
					return new ApiResponse(400, msg, null);
				}
				
				/// Checking company details
				CompanyDetails comp = companyDetailsRepository.findByCompanyId(printDto.getCompanyCode());
				if (comp == null) {
					resp.setMessage("Invalid company code.");
					resp.setStatusCode(400);
					return resp;
				}

				PrintJobMaster print = null;
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
				ProductMaster product = productMasterRepository.findByProductNameAndPackSize(printDto.getProductName(),printDto.getPackSize());

				print.setProductName(printDto.getProductName());
				print.setPackSize(printDto.getPackSize());
				print.setPackUnit(printDto.getPackUnit());
				print.setManufactureDate(printDto.getManufactureDate());
				print.setExpiryDate(printDto.getExpiryDate());
				print.setBatchNumber(printDto.getBatchNumber());
				print.setStartTime(LocalDateTime.now());
				print.setEndTime(LocalDateTime.now());
				print.setProductMaster(product);
				print.setSelectedTemplateName(printDto.getTemplateName().trim());
				print.setUserId(request.getHeader("userId"));
				print.setThreadId(Thread.currentThread().getId());
//				print.setUnitPrice(printDto.getUnitPrice()!=null?printDto.getUnitPrice():printDto.getProduct().getUnitPrice());
//				print.setUnitPrice(printDto.getUnitPrice() != null ? printDto.getUnitPrice() : product.getUnitPrice());
//				print.setUseShortUrl(printDto.getUseShortUrl());
//				print.setCompanyCode(printDto.getCompanyCode());
//				print.setProductMaster(productManager.getProductsByNameAndSize(printDto.getProductName(),printDto.getPackSize()+""));
//				print.setMrp(printDto.getMrp());
//				print.setProductMaster(printDto.getProduct());
//				print.setGtinNumber(printDto.getGtinNumber());

				log.info("==printDto.getPrintJobId()==BEFORE>" + printDto.getPrintJobId());
	            List<UniqueCodePrintedDataDetails> codesList = uniqueCodePrintedDataDetailsRepository.fetchUnusedCodes(printDto.getQtySatchesToPrint(), year,printDto.getProductionOrderNo(),printDto.getVariety());

				if (codesList != null && !codesList.isEmpty()) {
					if (printDto.getQtySatchesToPrint() <= codesList.size()) {
						printJobMasterRepository.save(print);
					} else {
						resp.setMessage("Not enough codes available to print.");
						resp.setStatusCode(404);
						return resp;
					}
				} else {
					resp.setResponse("Codes do not exist.");
					resp.setMessage("Codes do not exist.");
					resp.setStatusCode(404);
					return resp;
				}

			    PrinterMaster printerDetails = printerMasterRepository.findByLineNumberAndActiveTrue(printDto.getLineCode());
			    if (printerDetails == null) {
			        log.error("Printers Details are null. Unable to proceed with connect to Printers");
			        resp.setMessage("Printers details are missing. Cannot connect to the printers.");
			        resp.setStatusCode(500);
			        return resp;
			    } else {
			        log.info("LINE: " + printerDetails.getLineNumber() + "======>Printer Ip:" + printerDetails.getPrinterIp() + " and Printer Port:" + printerDetails.getPrinterPort());
			    }	
			    Socket printerSocket = null;
			    boolean printerConnectionSuccessful = false;
			    try {
			        // Retrieve socket for current line
			    	printerSocket = lineSocketMap.get(printDto.getLineCode());

			        // Check if socket is valid
			        if (printerSocket == null || printerSocket.isClosed() || !printerSocket.isConnected()) {
			            // Connect new socket
			        	printerSocket = connectToPrinter(printerDetails);
			            
			            if (printerSocket == null || !printerSocket.isConnected()) {
			                log.error("Failed to connect VideoJet Printer for line " + printDto.getLineCode());
			                resp.setMessage("VideoJet Printer Connection Failed for line " + printDto.getLineCode() + ". Please check IP: " + printerDetails.getPrinterIp() + " Port: " + printerDetails.getPrinterPort());
			                resp.setStatusCode(500);
			                return resp;
			            }
			            // Store the new connection in the map
			            lineSocketMap.put(printDto.getLineCode(), printerSocket);
			            log.info("New VideoJet Printer Connection established and stored for line: " + printDto.getLineCode());
			        } else {
			            log.info("Reusing existing VideoJet Printer Connection for line: " + printDto.getLineCode());
			        }
			        printerConnectionSuccessful = true;
			    } catch (Exception e) {
			        log.error("Exception in VideoJet Connection", e);
			        resp.setMessage("VideoJet Printer Connection Error: " + e.getMessage());
			        resp.setStatusCode(500);
			        return resp;
			    } 
			    
				//The below code to loop codes using thread
	   			List<UniqueCodePrintedDataDetails> emptyList = new ArrayList<>();
	   			PrintedCodesHistory codesHistory = new PrintedCodesHistory();
	   			DuplicatePrintCodes duplicatecodes = new DuplicatePrintCodes();
	   			PrintedCodes pc = new PrintedCodes();
			
				log.info("prntStFlag==>" + printerConnectionSuccessful);
				if (printerConnectionSuccessful) {
					startThreadBasedOnPrinterType(socket, print, emptyList, year, codesList, codesHistory,duplicatecodes, pc);
				} else {
					resp.setMessage("An unexpected error occurred while processing the request.");
					resp.setStatusCode(500);
					log.info("An unexpected error occurred while processing the thread request.");
					return resp;
				}
	   			
	   			JSONObject jsonResponse = new JSONObject();
	   			Object Count = uniqueCodePrintedDataDetailsRepository.getCountByPrintJobMasterId(+print.getId());
	   			jsonResponse.put("id", print.getId());
				jsonResponse.put("printedCount", Count);

				resp.setResponse(jsonResponse.toString());
				resp.setMessage("Success");
				resp.setStatusCode(200);
			}
		} catch (Exception e) {
			resp.setMessage("An unexpected error occurred while processing the request.");
			resp.setStatusCode(500);
			log.info("" + e.getStackTrace(), e);
		}
		return resp;
	}

	@SuppressWarnings("unchecked")
	public ApiResponse savePrintCodeDetails_UAT(HttpServletRequest request, @Valid PrintCodesRequest printDto) {
		ApiResponse resp = new ApiResponse();
		try {
			log.info("Phase======================================================1");
			synchronized (this) {
				log.info("=UAT=SYNCHRONIZED_BLOCK==" + printDto);
				int year = Calendar.getInstance().get(Calendar.YEAR);
				Long unusedCodes = uniqueCodePrintedDataDetailsRepository.getUnUsedCodesCount(printDto.getProductionOrderNo(),printDto.getVariety());
				log.info("Unused Codes Available: {}", unusedCodes);
				log.info("Phase======================================================2");

				// Inventory validation
				if (unusedCodes != null && unusedCodes > 0 && printDto.getQtySatchesToPrint() > unusedCodes) {
					String msg = "Inventory Exceeded. Available Codes: " + unusedCodes;
					return new ApiResponse(400, msg, null);
				}
				log.info("Phase======================================================3");

//				/// Checking company details
//				CompanyDetails comp = companyDetailsRepository.findByCompanyId(printDto.getCompanyCode());
//				if (comp == null) {
//					resp.setMessage("Invalid company code.");
//					resp.setStatusCode(400);
//					return resp;
//				}
				PrintJobMaster print=null;
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
//				ProductMaster product = productMasterRepository.findByProductNameAndPackSize(printDto.getProductName(),printDto.getPackSize());
				log.info("Phase======================================================4");

				print.setVariety(printDto.getVariety());
				print.setProductName(printDto.getProductName());
				print.setPackSize(printDto.getPackSize());
				print.setPackUnit(printDto.getPackUnit());
				print.setManufactureDate(printDto.getManufactureDate());
				print.setExpiryDate(printDto.getExpiryDate());
				print.setMrp(printDto.getMrp());
//				print.setProductMaster(product);
				print.setBatchNumber(printDto.getBatchNumber());
				print.setStartTime(LocalDateTime.now());
				print.setEndTime(LocalDateTime.now());
				print.setCompanyCode(printDto.getCompanyCode());
				print.setSelectedTemplateName(printDto.getTemplateName().trim());
				print.setUserId(request.getHeader("userId"));
				print.setThreadId(Thread.currentThread().getId());
				print.setStatus("UAT");
				print.setUnitPrice(printDto.getUnitPrice());

//				print.setProductMaster(printDto.getProduct());
//				print.setGtinNumber(printDto.getGtinNumber());
				print.setUseShortUrl(printDto.getUseShortUrl());
				log.info("Phase======================================================5");

				log.info("==printDto.getPrintJobId()==BEFORE>" + printDto.getPrintJobId());
//	            List<UniqueCodePrintedDataDetails> codesList = uniqueCodePrintedDataDetailsRepository.fetchUnusedCodes(printDto.getQtySatchesToPrint(), year);
	            List<UniqueCodePrintedDataDetails> codesList = uniqueCodePrintedDataDetailsRepository.fetchUnusedCodes(printDto.getQtySatchesToPrint(), year,printDto.getProductionOrderNo(),printDto.getVariety());

				log.info("Phase======================================================6");

	            if (codesList != null && !codesList.isEmpty()) {
					log.info("Phase======================================================6.15====>"+codesList.size());

					log.info("Phase======================================================6.25");
					if (printDto.getQtySatchesToPrint() <= codesList.size()) {
						log.info("Phase======================================================6.50");
						printJobMasterRepository.save(print);
					} else {
						log.info("Phase======================================================6.75");
						resp.setMessage("Not enough codes available to print.");
						resp.setStatusCode(404);
						return resp;
					}
				} else {
					resp.setResponse("Codes do not exist.");
					resp.setMessage("Codes do not exist.");
					resp.setStatusCode(404);
					return resp;
				}
				log.info("Phase======================================================7");

				log.info("UAT_socket===>" + socket);
				boolean prntStFlag = true;
				boolean concFlag = true;

				while (!concFlag) {
					if (socket != null && socket.isConnected()) {
						concFlag = true;
						log.info("UAT_socket.isConnected()==>" + socket.isConnected());
						log.info("Phase======================================================8");

						// Checking printerStatus
						while (!prntStFlag) {
							prntStFlag = getNetWorkPrinterStatus(socket, print.getSelectedTemplateName());
						}
					} else {
						log.info("Phase======================================================9");

						socket = conectToNetWorkPrinter();
						Thread.sleep(2000);
						if (socket != null && socket.isConnected()) {
							log.info("UAT_New socket Connection Established===>");
							concFlag = true;
							prntStFlag = true;
						}
					}
				}
				log.info("Phase======================================================10");

				/*
				 * if(socket==null) { resp.setResponse(appConfig.getProperty("ERROR_MESSAGE"));
				 * resp.setMessage(appConfig.getProperty("ERROR_MESSAGE"));
				 * resp.setStatusCode(appConfig.getProperty("ERROR_CODE")); return resp; }
				 */

				// The below code to loop codes using thread
				List<UniqueCodePrintedDataDetails> emptyList = new ArrayList<>();
				PrintedCodesHistory codesHistory = new PrintedCodesHistory();
				DuplicatePrintCodes duplicatecodes = new DuplicatePrintCodes();
				PrintedCodes pc = new PrintedCodes();
				log.info("Phase======================================================11");

				log.info("UAT_prntStFlag==>" + prntStFlag);
				if (prntStFlag) {
					startThreadBasedForUAT(print, emptyList, year, codesList, codesHistory, duplicatecodes, pc);
				} else {
					resp.setMessage("An unexpected error occurred while processing the request.");
					resp.setStatusCode(500);
					log.info("An unexpected error occurred while processing the thread request.");
					return resp;
				}
				log.info("Phase======================================================12");

				JSONObject jsonResponse = new JSONObject();
	   			Object Count = uniqueCodePrintedDataDetailsRepository.getCountByPrintJobMasterId(+print.getId());
				jsonResponse.put("id", print.getId());
				jsonResponse.put("printedCount", Count);

				resp.setMessage("Success");
				resp.setStatusCode(200);
			}
		} catch (Exception e) {
			resp.setMessage("An unexpected error occurred while processing the request.");
			resp.setStatusCode(500);
			log.info("An unexpected error occurred while processing the thread request.");
			return resp;
		}

		return resp;
	}

	public Socket connectToPrinter(PrinterMaster printerDetails) {
		Socket socket = null;
		try {
			socket = new Socket();
			socket.connect(new InetSocketAddress(printerDetails.getPrinterIp(), (printerDetails.getPrinterPort())), 2000 // 2 seconds connection timeout
			);
			socket.setSoTimeout(3000); // 3 seconds read timeout
			// Quick connection validation
			if (socket.isConnected()) {
				log.info("Printer Connected - Line: {}, Printer Name: {}", printerDetails.getLineNumber(), printerDetails.getPrinterName());
				return socket;
			}
		} catch (IOException e) {
			log.error("Printer Connection Failed: " + e.getMessage());
			// Immediate socket closure
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException closeEx) {
					log.error("Socket Close Error", closeEx);
				}
			}
		}
		return null;
	}

	public void closeConnections(Socket printerIp) {
		log.info("---------------closeConnections----------------");
		try {
			if (printerIp.isConnected()) {
				printerIp.close();
				log.info("-----------videojetSocket Connection Closed Sucessfully-----------------");
			}
			log.info("---------All--connection ClosedSucessfully-----------------");
		} catch (IOException e) {
			log.info("--------ERROR-------Closed socket connection for line-----------------" + e);
		}
	}

	public void closeLineSocket(String lineCode) {
		Socket socket = lineSocketMap.remove(lineCode);
		if (socket != null && !socket.isClosed()) {
			try {
				socket.close();
				log.info("Closed socket connection for line" + lineCode);
			} catch (IOException e) {
				log.error("Error closing socket for line:}" + lineCode, e);
			}
		}
	}

	public String startThreadBasedOnPrinterType(Socket socket, PrintJobMaster print,List<UniqueCodePrintedDataDetails> emptyList, int year, List<UniqueCodePrintedDataDetails> codesList,PrintedCodesHistory codesHistory, DuplicatePrintCodes duplicatecodes, PrintedCodes pc) {
		if ("DOMINO_TPO_V".equalsIgnoreCase(appConfig.getProperty("PRINTER_TYPE"))) {
//			 clearDominoPrinterBuffer(socket); 																									/** Clearing Buffer **/
			log.info("IT IS DOMINO PRINTER");
			log.info("==PRINTJOBID==" + print.getId() + "==AFTER_BUFFER_CLEARED==");
			PrintThreadServiceDominoPrinter printOprThred = new PrintThreadServiceDominoPrinter();
			printOprThred.setPrintedDataDetailsList(emptyList);
			printOprThred.setCurrentYear(year);
			printOprThred.setAppConfig(appConfig);
			printOprThred.setHibernateDao(hibernateDao);
			printOprThred.setPrintOperatorInterfaceDetails(print);
			printOprThred.setPrintedDataDetailsList(codesList);
			printOprThred.setUniqueCodePrintedDataDetailsRepository(uniqueCodePrintedDataDetailsRepository);
			printOprThred.setPrintJobMasterRepository(printJobMasterRepository);
			printOprThred.setUserId(print.getUserId());
			printOprThred.setPrintedCodesRepository(printedCodesRepository);
			printOprThred.setPrintedCodes(pc);
			printOprThred.setPrintedCodesHistoryRepository(printedCodesHistoryRepository);
			printOprThred.setPrintedCodesHistory(codesHistory);
			printOprThred.setDuplicatePrintCodesRepository(duplicatePrintCodesRepository);
			printOprThred.setDuplicatePrintCodes(duplicatecodes);
			PrintThreadServiceDominoPrinter.setStopThreadVariable(false);
			PrintThreadServiceDominoPrinter.setWhileLoopFlag(0);

			Thread thrd = new Thread(printOprThred);
			thrd.start();
		} else if ("DOMINO_TPO_VX".equalsIgnoreCase(appConfig.getProperty("PRINTER_TYPE"))) {
			clearDominoVxPrinterBuffer(socket); /** Clearing Buffer **/
			log.info("==PRINTJOBID==" + print.getId() + "==AFTER_BUFFER_CLEARED==");
			PrintThreadServiceDominoPrinterVx printOprThred = new PrintThreadServiceDominoPrinterVx();
			printOprThred.setPrintedDataDetailsList(emptyList);
			printOprThred.setCurrentYear(year);
			printOprThred.setAppConfig(appConfig);
			printOprThred.setHibernateDao(hibernateDao);
			printOprThred.setPrintOperatorInterfaceDetails(print);
			printOprThred.setPrintedDataDetailsList(codesList);
			printOprThred.setUniqueCodePrintedDataDetailsRepository(uniqueCodePrintedDataDetailsRepository);
			printOprThred.setPrintJobMasterRepository(printJobMasterRepository);
			printOprThred.setUserId(print.getUserId());
			printOprThred.setPrintedCodesRepository(printedCodesRepository);
			printOprThred.setPrintedCodes(pc);
			printOprThred.setPrintedCodesHistoryRepository(printedCodesHistoryRepository);
			printOprThred.setPrintedCodesHistory(codesHistory);
			printOprThred.setDuplicatePrintCodesRepository(duplicatePrintCodesRepository);
			printOprThred.setDuplicatePrintCodes(duplicatecodes);
			PrintThreadServiceDominoPrinterVx.setStopThreadVariable(false);
			PrintThreadServiceDominoPrinterVx.setWhileLoopFlag(0);
			Thread thrd = new Thread(printOprThred);
			thrd.start();
		}
		return "Success";
	}

	public boolean getNetWorkPrinterStatus(Socket socket, String templateName) {
		boolean prntStFlag = false;
		if ("VIDEOJET_TP".equalsIgnoreCase(appConfig.getProperty("PRINTER_TYPE"))) {
			int printerStatus = getVJTPrinterStatus(socket, templateName);
			if (printerStatus == 4 || printerStatus == 3) {
				prntStFlag = true;
			} else {
				String prntrStCmd = "SST|1|\r";
				sendCommandToPrinter(prntrStCmd, socket);

				String prntrStCmd1 = "SST|3|\r";
				sendCommandToPrinter(prntrStCmd1, socket);
			}
		} else if ("VIDEOJET_LP".equalsIgnoreCase(appConfig.getProperty("PRINTER_TYPE"))) {
			int printerStatus = getVJLPrinterStatus(socket, templateName);
			if (printerStatus == 1) {
				prntStFlag = true;
			} else {
				prntStFlag = false;
			}
		} else if ("DOMINO_TPO_V".equalsIgnoreCase(appConfig.getProperty("PRINTER_TYPE"))) {
			log.info("====PRINTER_TYPE=====" + appConfig.getProperty("PRINTER_TYPE"));
			int printerStatus = getDominoPrinterStatus_V(socket, templateName);
			if (printerStatus == 4000 || printerStatus == 4101 || printerStatus == 4102) {
				prntStFlag = true;
			}

		} else if ("DOMINO_TPO_VX".equalsIgnoreCase(appConfig.getProperty("PRINTER_TYPE"))) {
			int printerStatus = getDominoPrinterStatus_Vx(socket, templateName);
			if (printerStatus == 0) {
				prntStFlag = true;
			}

		} else if ("MARKEM_IMAGE".equalsIgnoreCase(appConfig.getProperty("PRINTER_TYPE"))) {
			int printerStatus = getMIPrinterStatus();
			if (printerStatus > 0) {
				prntStFlag = true;
			} else {
				prntStFlag = false;
			}
		}

		return prntStFlag;
	}

	public int getVJTPrinterStatus(Socket socket, String templatename) {
		int status = 0;

		String statusCmd = "GST\r";
		String resp = sendCommandToPrinter(statusCmd, socket);
		log.info("getVJThermalPrinterStatus==>" + resp);
		int prnstatus = 0;
		int err = 0;
		if (resp.contains("STS")) {
			prnstatus = Integer.parseInt(resp.split("\\|")[1]);
			err = Integer.parseInt(resp.split("\\|")[2]);
		} else {
			getVJTPrinterStatus(socket, templatename);
		}
		if (err == 2) {
			String selDupTemp = "SEL|" + templatename + "|\r";
			sendCommandToPrinter(selDupTemp, socket);
			String clrErr = "CAF\r";
			sendCommandToPrinter(clrErr, socket);
			String clrWrng = "CAW\r";
			sendCommandToPrinter(clrWrng, socket);
			String selTemp = "SEL|" + templatename + "|\r";
			sendCommandToPrinter(selTemp, socket);
		}
		if (prnstatus == 4) {

			String selDupTemp = "SEL|" + templatename + "|\r";
			sendCommandToPrinter(selDupTemp, socket);
			String clrErr = "CAF\r";
			sendCommandToPrinter(clrErr, socket);
			String clrWrng = "CAW\r";
			sendCommandToPrinter(clrWrng, socket);

			String setOnlineCmd = "SST|3|\r";
			sendCommandToPrinter(setOnlineCmd, socket);
			String selTemp = "SEL|" + templatename + "|\r";
			sendCommandToPrinter(selTemp, socket);
		}
		status = prnstatus;
		log.info("GET_PRINTER_STATUS" + status);
		return status;
	}

	@SuppressWarnings("unused")
	public int getVJLPrinterStatus(Socket socket, String templatename) {
		int status = 0;

		String statusCmd = "GetStatus;\r\n";
		String resp = sendCommandToPrinter(statusCmd, socket);
		log.info("VJLP_StatusCmd_Resp==>" + resp);
		int prnstatus = 0;
		int err = 0;
		if (resp.contains("0000;")) {
			prnstatus = 1;
		} else {
			getVJLPrinterStatus(socket, templatename);
		}

		status = prnstatus;
		log.info("PRINTER_STATUS  ::::" + status);
		return status;
	}

	static char SOH = (char) 1;
	static char STX = (char) 2;
	static char ETX = (char) 3;
	static char ETB = (char) 23;

	public int getDominoPrinterStatus_V(Socket socket, String templatename) {
		int status = 0;
		String statusCmd = SOH + "RequestStatus=" + STX + "CurrentPage" + ETX + ETB;
		String resp = sendCommandToPrinter(statusCmd, socket);
		log.info("dominoPrinterStatus_CmdResp_V==>" + resp);// AnswerStatus=4000,0,0,Ready,,,,,001,0,0,2,0,0,0,0,1
		int prnstatus = 0;
		if (resp.contains("AnswerStatus")) {
			String[] response = resp.split("=");
			prnstatus = Integer.parseInt(response[1].split(",")[0]);
			if (prnstatus == 4000 || prnstatus == 4101 || prnstatus == 4102) {
			}
			/*
			 * else { statusCmd = SOH + "AckError=0" + ETB; sendCommandToPrinter(statusCmd,
			 * socket); //getDominoPrinterStatus_V(socket, templatename); }
			 */
		} else {
			getDominoPrinterStatus_V(socket, templatename);
		}

		status = prnstatus;
		log.info("GET_DOMINO_PRINTER_STATUS" + status);
		return status;
	}

	public int getDominoPrinterStatus_Vx(Socket socket, String templatename) {
		int status = 0;
		String statusCmd = "GETSTATUS\r\n";// RESULT GETSTATUS 0 6000 "OK"
		String resp = sendCommandToPrinter(statusCmd, socket);
		log.info("dominoPrinterStatus_CmdResp==>" + resp);
		int prnstatus = 0;
		if (resp.contains("RESULT GETSTATUS")) {
			String[] response = resp.split(" ");
			prnstatus = Integer.parseInt(response[2]);
			if (prnstatus == 0) {
			}
		} else {
			getDominoPrinterStatus_Vx(socket, templatename);
		}

		status = prnstatus;
		log.info("GET_DOMINO_PRINTER_STATUS" + status);
		return status;
	}
	
	@SuppressWarnings("unused")
	public int getMIPrinterStatus()
	{
		int status=0;
		String printerIp=appConfig.getProperty("PRINTER_IP");
				int port=Integer.parseInt(appConfig.getProperty("PRINTER_PORT"));
		try {
		    Socket printerSocket = new Socket(printerIp, port);
		    OutputStream printerOutputStream = printerSocket.getOutputStream();
		    InputStream printerInputStream = printerSocket.getInputStream();
		    String command = STX + "~DR|" + ETX;;
		    byte[] commandBytes = command.getBytes();
		    printerOutputStream.write(commandBytes);
		    printerOutputStream.flush();

		    byte[] responseBytes = new byte[1024]; // Adjust the buffer size as needed
		    int bytesRead = printerInputStream.read(responseBytes);
		    String response = new String(responseBytes, 0, bytesRead);

		    printerSocket.close();
		    
		    printerSocket.close();
		    return status=1;
		} catch (IOException e) {
		    e.printStackTrace();
		}
		return status;
	}
	
	public String sendCommandToPrinter(String sndCmd,Socket socket)
	{
		String resp="";
		OutputStream output= null;
		if(socket!=null)
		{
			log.info("IS_SOCKET_CONNECTED==>"+socket.isConnected());
			try
			{
				BufferedReader reader= null;
				output = socket.getOutputStream();
		        reader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"utf-8"));
				output.write(sndCmd.getBytes());
				String response = reader.readLine();
				resp= response.toString();
			}
			catch(Exception e)
			{
				log.info("Exec==>"+e.getStackTrace(),e);
				resp="ERROR";
				try{
				 socket.close();
				 log.info("==SOCKET CLOSED==");
				}catch(Exception ex)
				{
					log.info("SOCKET CLOSE EXCEPTION ==>"+e.getStackTrace(),ex);
				}
			}
		}
		return resp;
	}

	public String setPrinterRunning() {
		String setOnlineCmd = "SST|3|\r";
		String resp = "";
		OutputStream output = null;
		if (socket != null) {
			try {
				BufferedReader reader = null;
				output = socket.getOutputStream();
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
				output.write(setOnlineCmd.getBytes());
				String response = reader.readLine();
				resp = response.toString();
			} catch (Exception e) {
				log.info("Exec==>" + e.getStackTrace(), e);
				resp = "ERROR";
			}
		}
		return resp;
	}

	public String clearVJTPrinterBuffer(Socket socket) {
		String resp = "";
		try {
			String tempSelCmd = "CQI\r";
			OutputStream output = socket.getOutputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
			output.write(tempSelCmd.getBytes());
			String response = reader.readLine();
			resp = response.toString();
		} catch (Exception e) {
			log.info("clearVJTPrinterBuffer_Exec==>" + e.getStackTrace(), e);
			resp = "ERROR";
		}
		return resp;
	}

	public String clearVJLPrinterBuffer(Socket socket) {
		String resp = "";
		try {
			String tempSelCmd = "ClearBufferData;\r\n";
			OutputStream output = socket.getOutputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
			output.write(tempSelCmd.getBytes());
			String response = reader.readLine();
			resp = response.toString();
		} catch (Exception e) {
			log.info("clearVJLPrinterBuffer_Exec==>" + e.getStackTrace(), e);
			resp = "ERROR";
		}
		return resp;
	}

	public String clearDominoVxPrinterBuffer(Socket socket) {
		String resp = "";
		try {
			String tempSelCmd = "BUFFERCLEAR\r\n";
			OutputStream output = socket.getOutputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
			output.write(tempSelCmd.getBytes());
			String response = reader.readLine();
			resp = response.toString();
		} catch (Exception e) {
			log.info("clearDominoPrinterBuffer_Exec==>" + e.getStackTrace(), e);
			resp = "ERROR";
		}
		return resp;
	}

	public String clearMIPrinterBuffer(Socket socket) {
		String resp = "";
		try {
			String tempSelCmd = "{~DC0|}";// <ClearPackDataQueue act=50001/>
			OutputStream output = socket.getOutputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
			output.write(tempSelCmd.getBytes());
			String response = reader.readLine();
			resp = response.toString();
		} catch (Exception e) {
			log.info("clearMIPrinterBuffer_Exec==>" + e.getStackTrace(), e);
			resp = "ERROR";
		}
		return resp;
	}

	public Socket conectToNetWorkPrinter() {
		Socket socket = null;
		try {
			socket = new Socket(appConfig.getProperty("PRINTER_IP"),Integer.parseInt(appConfig.getProperty("PRINTER_PORT")));
			socket.setSoTimeout(5000);
			log.info("==SOCKET CONNECTED==");
		} catch (Exception e) {
			log.info("==SOCKET NOT CONNECTED==");
			log.info("ConnectToPrinter==>" + e.toString());
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			if (socket != null && !socket.isClosed()) {
				try {
					socket.close();
					log.info("==SOCKET CONNECTION CLOSED forcefully==");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return socket;
	}
	
	public String startThreadBasedForUAT(PrintJobMaster print, List<UniqueCodePrintedDataDetails> emptyList, int year,List<UniqueCodePrintedDataDetails> codesList, PrintedCodesHistory codesHistory, DuplicatePrintCodes duplicatecodes,PrintedCodes pc) {
		// Clear buffer
		log.info("UAT==PRINTJOBID==" + print.getId());
		// The below code to loop codes using thread
		PrintThreadServiceWorkingForUAT printOprThred = new PrintThreadServiceWorkingForUAT();
		printOprThred.setPrintedDataDetailsList(emptyList);
		printOprThred.setCurrentYear(year);
		printOprThred.setAppConfig(appConfig);
		printOprThred.setHibernateDao(hibernateDao);
		printOprThred.setPrintOperatorInterfaceDetails(print);
		printOprThred.setPrintedDataDetailsList(codesList);
		printOprThred.setUniqueCodePrintedDataDetailsRepository(uniqueCodePrintedDataDetailsRepository);
		printOprThred.setPrintJobMasterRepository(printJobMasterRepository);
		printOprThred.setUserId(print.getUserId());
		printOprThred.setPrintedCodesRepository(printedCodesRepository);
		printOprThred.setPrintedCodes(pc);
		printOprThred.setPrintedCodesHistoryRepository(printedCodesHistoryRepository);
		printOprThred.setPrintedCodesHistory(codesHistory);
		printOprThred.setDuplicatePrintCodesRepository(duplicatePrintCodesRepository);
		printOprThred.setDuplicatePrintCodes(duplicatecodes);
		PrintThreadServiceWorkingForUAT.setStopThreadVariable(false);
		PrintThreadServiceWorkingForUAT.setWhileLoopFlag(0);

		Thread thrd = new Thread(printOprThred);
		thrd.start();

		// Print codes using normal for loop
		/*
		 * forLoopFlag = true; String printResp =
		 * printQRCodesWithForloopCondition(codesList,print,year,socket);
		 * log.info("==PRINTJOBID=="+print.getId()+"==printResp==>"+printResp);
		 */
		return "Success";
	}

	@SuppressWarnings({ "unused" })
	public ApiResponse stopPrinting(HttpServletRequest request, String jsonData) {
		ApiResponse resp = new ApiResponse();
		try {
			JSONObject mainData = new JSONObject(jsonData);
			long id = mainData.getLong("id");
			log.info("PRINTJOB_ID=stopPrinting=>" + id);
			Optional<PrintJobMaster> print = printJobMasterRepository.findById(id);

			if (socket != null) {
				log.info("== SOCKET CONNECTION NOT AVAILABLE ==");
				setPrinterOffline();
				// socket.close();
				// socket=null;
			}

			forLoopFlag = false;
			resp.setMessage("Success");
			resp.setStatusCode(200);
		} catch (Exception e) {
			resp.setMessage("An unexpected error occurred while processing the request.");
			resp.setStatusCode(500);
			log.info("" + e.getStackTrace(), e);
		}
		return resp;
	}

	public String setPrinterOffline() {
		String resp = "";
		try {
//			if ("VIDEOJET_TP".equalsIgnoreCase(appConfig.getProperty("PRINTER_TYPE"))) {
//				String offlineCmd = "SST|4|\r";
//				OutputStream output = null;
//				PrintThreadServiceWorking thrd = new PrintThreadServiceWorking();
//				List<PrintedDataDetails> emptyList = new ArrayList<>();
//				thrd.setPrintedDataDetailsList(emptyList);
//				thrd.setStopThreadVariable(true);
//
//				PrintThreadServiceWorking.setStopThreadVariable(true);
//				PrintThreadServiceWorking.setWhileLoopFlag(1);
//				if (socket != null) {
//					try {
//						BufferedReader reader = null;
//						output = socket.getOutputStream();
//						reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
//						output.write(offlineCmd.getBytes());
//						String response = reader.readLine();
//						resp = response.toString();
//					} catch (Exception e) {
//						log.info("Exec==>" + e.getStackTrace(), e);
//						resp = "ERROR";
//					}
//				}
//			} else if ("VIDEOJET_LP".equalsIgnoreCase(appConfig.getProperty("PRINTER_TYPE"))) {
//				String offlineCmd = "Stop;\r\n";
//				OutputStream output = null;
//				PrintThreadServiceVideoJetLaserPrinter thrd = new PrintThreadServiceVideoJetLaserPrinter();
//				List<PrintedDataDetails> emptyList = new ArrayList<>();
//				thrd.setPrintedDataDetailsList(emptyList);
//				thrd.setStopThreadVariable(true);
//
//				PrintThreadServiceVideoJetLaserPrinter.setStopThreadVariable(true);
//				PrintThreadServiceVideoJetLaserPrinter.setWhileLoopFlag(1);
//				if (socket != null) {
//					try {
//						BufferedReader reader = null;
//						output = socket.getOutputStream();
//						reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
//						output.write(offlineCmd.getBytes());
//						String response = reader.readLine();
//						resp = response.toString();
//					} catch (Exception e) {
//						log.info("Exec==>" + e.getStackTrace(), e);
//						resp = "ERROR";
//					}
//				}
//			} else 
//				
				
				if ("DOMINO_TPO_V".equalsIgnoreCase(appConfig.getProperty("PRINTER_TYPE"))) {
				String offlineCmd = SOH + "PausePrint" + ETB;
				;
				OutputStream output = null;
				PrintThreadServiceDominoPrinter thrd = new PrintThreadServiceDominoPrinter();
				List<UniqueCodePrintedDataDetails> emptyList = new ArrayList<>();
				thrd.setPrintedDataDetailsList(emptyList);
				thrd.setStopThreadVariable(true);

				PrintThreadServiceDominoPrinter.setStopThreadVariable(true);
				PrintThreadServiceDominoPrinter.setWhileLoopFlag(1);
				if (socket != null) {
					try {
						BufferedReader reader = null;
						output = socket.getOutputStream();
						reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
						output.write(offlineCmd.getBytes());
						String response = reader.readLine();
						resp = response.toString();
					} catch (Exception e) {
						log.info("Exec==>" + e.getStackTrace(), e);
						resp = "ERROR";
					}
				}
			} else if ("DOMINO_TPO_VX".equalsIgnoreCase(appConfig.getProperty("PRINTER_TYPE"))) {
				log.info("ENTER TO SET PRINTER OFFLINE===========>");
				String offlineCmd = "BUFFERCLEAR\r\n";
				OutputStream output = null;
				PrintThreadServiceDominoPrinterVx thrd = new PrintThreadServiceDominoPrinterVx();
				List<UniqueCodePrintedDataDetails> emptyList = new ArrayList<>();
				thrd.setPrintedDataDetailsList(emptyList);
				thrd.setStopThreadVariable(true);
				PrintThreadServiceDominoPrinterVx.setStopThreadVariable(true);
				PrintThreadServiceDominoPrinterVx.setWhileLoopFlag(1);
				log.info("=== WHILE LOOP FLAG ===::" + PrintThreadServiceDominoPrinterVx.getWhileLoopFlag());
				/*
				 * if(socket!=null) { try { BufferedReader reader= null; output =
				 * socket.getOutputStream(); reader = new BufferedReader(new
				 * InputStreamReader(socket.getInputStream(),"utf-8"));
				 * output.write(offlineCmd.getBytes()); String response = reader.readLine();
				 * resp= response.toString(); } catch(Exception e) {
				 * log.info("Exec==>"+e.getStackTrace(),e); resp="ERROR"; } }
				 */
			}
			
//			else if ("MARKEM_IMAGE".equalsIgnoreCase(appConfig.getProperty("PRINTER_TYPE"))) {
//				log.info("ENTER TO SET PRINTER OFFLINE===========>");
//				PrintThreadServiceMIPrinter thrd = new PrintThreadServiceMIPrinter();
//				List<PrintedDataDetails> emptyList = new ArrayList<>();
//				thrd.setPrintedDataDetailsList(emptyList);
//				thrd.setStopThreadVariable(true);
//				PrintThreadServiceMIPrinter.setStopThreadVariable(true);
//				PrintThreadServiceMIPrinter.setWhileLoopFlag(1);
//				log.info("Markem Machine Stopped===========>");
//
//				log.info("=== WHILE LOOP FLAG ===::" + PrintThreadServiceMIPrinter.getWhileLoopFlag());
//			}
		} catch (Exception e) {
			log.info("Final Exec==>" + e.getStackTrace(), e);
		}
		return resp;
	}

	public ApiResponse getPrintCodeStatus(HttpServletRequest request, String reqObj) {
			ApiResponse responseDTO = new ApiResponse();
			try {
				JSONObject mainData = new JSONObject(reqObj);
				long id = mainData.getLong("id");
				Object Count = uniqueCodePrintedDataDetailsRepository.getCountByPrintJobMasterId(id);
				Optional<PrintJobMaster> pj = printJobMasterRepository.findById(id);

				// int Count = getBatchCountFromPrinter();
				log.info("Current Batch Printed Count ::::" + Count);
				responseDTO.setResponse(Count);
//				responseDTO.setDraw(pj != null ? pj.getStatus() : "NA");
				responseDTO.setMessage("Success");
				responseDTO.setStatusCode(200);
			} catch (Exception e) {
				responseDTO.setMessage("An unexpected error occurred while processing the request.");
				responseDTO.setStatusCode(500);
				log.info("" + e.getStackTrace(), e);
			}
			return responseDTO;
		}

		public ApiResponse generateUniqueCodes(HttpServletRequest request, String jsonData) {
			try {
				JSONObject json = new JSONObject(jsonData);
				log.info("quantity===================0=========>");

				int quantity = json.getInt("quantity");
				log.info("quantity============================>"+quantity);
				
				if (quantity > 50000) {
				    return new ApiResponse(400,"Request exceeds limit. Maximum 50,000 codes can be generated at a time",null);
				}
				log.info("quantity======================2======>"+quantity);

				Long lastSerial = uniqueCodePrintedDataDetailsRepository.findMaxSerialNumber();
				if (lastSerial == null) {
					lastSerial = 1000000000L - 1;
				}
				log.info("quantity==============3==============>"+quantity);

//				String plantCode = appConfig.getProperty("PLANT_CODE");
//				String lineNumber = appConfig.getProperty("LINE_NUMBER");
				log.info("quantity================4===========>"+quantity);

				List<UniqueCodePrintedDataDetails> batch = new ArrayList<>();

				for (int i = 1; i <= quantity; i++) {
					UniqueCodePrintedDataDetails uniqueCodePrintDetails = new UniqueCodePrintedDataDetails();

					String uid = UIDGenerator.generateUID(plantCode);
					uniqueCodePrintDetails.setUidCode(uid);
					uniqueCodePrintDetails.setSerialNumber(lastSerial + i);
					uniqueCodePrintDetails.setCreatedOn(LocalDateTime.now());
					uniqueCodePrintDetails.setActive(true);
					uniqueCodePrintDetails.setCodesYear(LocalDateTime.now().getYear());
					uniqueCodePrintDetails.setLineNumber(lineNumber);
					batch.add(uniqueCodePrintDetails);
					log.info("quantity=======5====================>"+quantity);

					if (batch.size() == 1000) {
						uniqueCodePrintedDataDetailsRepository.saveAll(batch);
						batch.clear();
					}
				}
				if (!batch.isEmpty()) {
					uniqueCodePrintedDataDetailsRepository.saveAll(batch);
				}
				log.info("quantity==========6=================>"+quantity);
				return new ApiResponse(200, "Unique codes generated successfully",quantity + " codes generated for Line : " + lineNumber);
			} catch (Exception e) {
				log.error("Exception in generateUniqueCodes:", e);
				return new ApiResponse(500, "Error occurred while generating unique codes", e.getMessage());
			}
		}

		public ApiResponse getUsedAndUnusedCodesCount(HttpServletRequest request) {
			try {
				Long usedCount = uniqueCodePrintedDataDetailsRepository.getAllUsedCountTillDate();
				Long unusedCount = uniqueCodePrintedDataDetailsRepository.getAllUnusedCountTillDate();

				Map<String, Long> responseMap = new HashMap<>();
				responseMap.put("usedCodesCount", usedCount != null ? usedCount : 0L);
				responseMap.put("unusedCodesCount", unusedCount != null ? unusedCount : 0L);
				return new ApiResponse(200, "Counts fetched successfully", responseMap);
			} catch (Exception e) {
				e.printStackTrace();
				return new ApiResponse(500, "Error fetching used/unused codes count: " + e.getMessage(), null);
			}
		}
	}
