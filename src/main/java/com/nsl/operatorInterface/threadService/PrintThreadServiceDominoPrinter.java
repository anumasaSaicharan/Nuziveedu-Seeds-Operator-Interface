package com.nsl.operatorInterface.threadService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nsl.operatorInterface.entity.DuplicatePrintCodes;
import com.nsl.operatorInterface.entity.PrintJobMaster;
import com.nsl.operatorInterface.entity.PrintedCodes;
import com.nsl.operatorInterface.entity.PrintedCodesHistory;
import com.nsl.operatorInterface.entity.UniqueCodePrintedDataDetails;
import com.nsl.operatorInterface.repository.DuplicatePrintCodesRepository;
import com.nsl.operatorInterface.repository.PrintJobMasterRepository;
import com.nsl.operatorInterface.repository.PrintedCodesHistoryRepository;
import com.nsl.operatorInterface.repository.PrintedCodesRepository;
import com.nsl.operatorInterface.repository.UniqueCodePrintedDataDetailsRepository;
import com.nsl.operatorInterface.service.impl.QRCodeService;
import com.nsl.operatorInterface.utility.HibernateDao;

import lombok.extern.slf4j.Slf4j;



@Slf4j
@Component
public class PrintThreadServiceDominoPrinter implements Runnable {

	protected HibernateDao hibernateDao;
	private Environment appConfig;
	private UniqueCodePrintedDataDetailsRepository uniqueCodePrintedDataDetailsRepository;
	private List<UniqueCodePrintedDataDetails> printedDataDetailsList;
	private PrintJobMaster printOperatorInterfaceDetails;
	private PrintJobMasterRepository printJobMasterRepository;
	private static Socket socket;
	private String userId;
	private PrintedCodesRepository printedCodesRepository;
	private PrintedCodes printedCodes;
	private PrintedCodesHistory printedCodesHistory;
	private PrintedCodesHistoryRepository printedCodesHistoryRepository;
	private DuplicatePrintCodes duplicatePrintCodes;
	private DuplicatePrintCodesRepository duplicatePrintCodesRepository;
	private int currentYear;
	private QRCodeService qrCodeService;
	private static boolean stopThreadVariable = false;
	private static int whileLoopFlag = 0;
	private static final DateTimeFormatter DD_MMM_YYYY_FORMAT = DateTimeFormatter.ofPattern("dd.MMM.yyyy");
	public static final DateTimeFormatter YYMMDD_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");
	
	public PrintedCodes getPrintedCodes() {
		return printedCodes;
	}

	public void setPrintedCodes(PrintedCodes printedCodes) {
		this.printedCodes = printedCodes;
	}

	public PrintedCodesHistory getPrintedCodesHistory() {
		return printedCodesHistory;
	}

	public void setPrintedCodesHistory(PrintedCodesHistory printedCodesHistory) {
		this.printedCodesHistory = printedCodesHistory;
	}

	public DuplicatePrintCodes getDuplicatePrintCodes() {
		return duplicatePrintCodes;
	}

	public void setDuplicatePrintCodes(DuplicatePrintCodes duplicatePrintCodes) {
		this.duplicatePrintCodes = duplicatePrintCodes;
	}

	public int getCurrentYear() {
		return currentYear;
	}

	public void setCurrentYear(int currentYear) {
		this.currentYear = currentYear;
	}

	public static int getWhileLoopFlag() {
		return whileLoopFlag;
	}

	public static void setWhileLoopFlag(int whileLoopFlag) {
		PrintThreadServiceDominoPrinter.whileLoopFlag = whileLoopFlag;
	}

	public static boolean isStopThreadVariable() {
		return stopThreadVariable;
	}

	public static void setStopThreadVariable(boolean stopThreadVariable) {
		PrintThreadServiceDominoPrinter.stopThreadVariable = stopThreadVariable;
	}

	public PrintThreadServiceDominoPrinter() {

	}

	public HibernateDao getHibernateDao() {
		return hibernateDao;
	}

	public void setHibernateDao(HibernateDao hibernateDao) {
		this.hibernateDao = hibernateDao;
	}

	public Environment getAppConfig() {
		return appConfig;
	}

	public void setAppConfig(Environment appConfig) {
		this.appConfig = appConfig;
	}

	public PrintedCodesRepository getPrintedCodesRepository() {
		return printedCodesRepository;
	}

	public void setPrintedCodesRepository(PrintedCodesRepository printedCodesRepository) {
		this.printedCodesRepository = printedCodesRepository;
	}

	public UniqueCodePrintedDataDetailsRepository getUniqueCodePrintedDataDetailsRepository() {
		return uniqueCodePrintedDataDetailsRepository;
	}

	public void setUniqueCodePrintedDataDetailsRepository(
			UniqueCodePrintedDataDetailsRepository uniqueCodePrintedDataDetailsRepository) {
		this.uniqueCodePrintedDataDetailsRepository = uniqueCodePrintedDataDetailsRepository;
	}

	public PrintJobMaster getPrintOperatorInterfaceDetails() {
		return printOperatorInterfaceDetails;
	}

	public void setPrintOperatorInterfaceDetails(PrintJobMaster printOperatorInterfaceDetails) {
		this.printOperatorInterfaceDetails = printOperatorInterfaceDetails;
	}

	public PrintJobMasterRepository getPrintJobMasterRepository() {
		return printJobMasterRepository;
	}

	public void setPrintJobMasterRepository(PrintJobMasterRepository printJobMasterRepository) {
		this.printJobMasterRepository = printJobMasterRepository;
	}

	public static Socket getSocket() {
		return socket;
	}

	public static void setSocket(Socket socket) {
		PrintThreadServiceDominoPrinter.socket = socket;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public PrintedCodesHistoryRepository getPrintedCodesHistoryRepository() {
		return printedCodesHistoryRepository;
	}

	public void setPrintedCodesHistoryRepository(PrintedCodesHistoryRepository printedCodesHistoryRepository) {
		this.printedCodesHistoryRepository = printedCodesHistoryRepository;
	}

	public DuplicatePrintCodesRepository getDuplicatePrintCodesRepository() {
		return duplicatePrintCodesRepository;
	}

	public void setDuplicatePrintCodesRepository(DuplicatePrintCodesRepository duplicatePrintCodesRepository) {
		this.duplicatePrintCodesRepository = duplicatePrintCodesRepository;
	}

	public QRCodeService getQrCodeService() {
		return qrCodeService;
	}

	public void setQrCodeService(QRCodeService qrCodeService) {
		this.qrCodeService = qrCodeService;
	}

	public void setPrintedDataDetailsList(List<UniqueCodePrintedDataDetails> printedDataDetailsList) {
		this.printedDataDetailsList = printedDataDetailsList;
	}

	public List<UniqueCodePrintedDataDetails> getPrintedDataDetailsList() {
		return printedDataDetailsList;
	}

	static char SOH = (char) 1;
	static char STX = (char) 2;
	static char ETX = (char) 3;
	static char ETB = (char) 23;

	public void run() {
		Session session = hibernateDao.openSession();
		try {
			synchronized (session) {

				socket = conectToNetWorkPrinter();
				Thread.sleep(2000);
				if (socket != null && socket.isConnected()) {
					log.info("New socket Connection Established===>");
				}

							String batchNo=printOperatorInterfaceDetails.getBatchNumber();
							String mfgDt1 = printOperatorInterfaceDetails.getManufactureDate().format(DD_MMM_YYYY_FORMAT).toUpperCase();
							String expDt1 = printOperatorInterfaceDetails.getExpiryDate().format(DD_MMM_YYYY_FORMAT).toUpperCase();
							String mrp= String.format("%.2f",printOperatorInterfaceDetails.getMrp().doubleValue());
							String unitPrice=String.format("%.2f",printOperatorInterfaceDetails.getUnitPrice().doubleValue());
							BigDecimal one = BigDecimal.ONE;
							int comparisonResult = printOperatorInterfaceDetails.getPackSize().compareTo(one); //==0 then value=1
							if(comparisonResult==0) {unitPrice="";}
							else {
							if("g".equalsIgnoreCase(printOperatorInterfaceDetails.getPackUnit())) {
								unitPrice=String.format("%.2f",printOperatorInterfaceDetails.getUnitPrice().doubleValue())+" Per gm";
							}
							else if("kg".equalsIgnoreCase(printOperatorInterfaceDetails.getPackUnit())) {
								unitPrice=String.format("%.2f",printOperatorInterfaceDetails.getUnitPrice().doubleValue())+" Per kg";
							}
							else if("ML".equalsIgnoreCase(printOperatorInterfaceDetails.getPackUnit())) {
								unitPrice=String.format("%.2f",printOperatorInterfaceDetails.getUnitPrice().doubleValue())+" Per ml";
							}
							else if("LT".equalsIgnoreCase(printOperatorInterfaceDetails.getPackUnit())) {
								unitPrice=String.format("%.2f",printOperatorInterfaceDetails.getUnitPrice().doubleValue())+" Per lt";
							}
							}
							
							String templateName = printOperatorInterfaceDetails.getSelectedTemplateName();
							log.info("==PRINT_JOB_ID=="+printOperatorInterfaceDetails.getId()+"==THREAD_STARTED");
							String tempSelCmd=SOH+"PrintDesign="+STX+templateName+".Design"+ETX+",0,0"+ETB;						                
							log.info("tempSelCmd---->"+tempSelCmd);
							boolean flag=true;
							
							boolean selCmdFlag=true;
							
							//Send pause print command to clear buffer
							/*String passPrintCmd=SOH+"PausePrint"+ETB;
							String passPrintCmdResp = sendCommandNoResponse(passPrintCmd,socket);
							log.info("passPrintCmd_Resp==>"+passPrintCmdResp);
							
							String resumePrintCmd=SOH+"ResumePrint"+ETB;
							String resumePrintCmdResp = sendCommandNoResponse(resumePrintCmd,socket);
							log.info("resumePrintCmd_Resp==>"+resumePrintCmdResp);
							*/
							
							while(selCmdFlag)
							{
								String resp = sendCommandToPrinter(tempSelCmd,socket);
								log.info("tempSelCmd_Resp==>"+resp);//AnswerPrintDesign=001.Design,1,0,0,0,PromptInfo,0, OR error								
									selCmdFlag=false;								
							}
							
							StringBuffer sb;
							int count=0;
							int noOfPrints=printedDataDetailsList.size();
							int balPrints=noOfPrints;
							int sentCount=0;
							if(noOfPrints>20)
								sentCount=20;
							else
								sentCount=noOfPrints;
							
							int totalSentCount=0;
							int queSize= getAvailablePrintCountsReadyToPrint(socket);
							log.info("==PRINT_JOB_ID=="+printOperatorInterfaceDetails.getId()+"==queSize==>"+queSize);
							
							boolean batchFlag=false;
							if(queSize<=20)
								batchFlag=true;
							
							
							if(queSize==-1) {
							stopThreadVariable=true;
							PrintThreadServiceDominoPrinter.setStopThreadVariable(true);
							PrintThreadServiceDominoPrinter.setWhileLoopFlag(1);
							}
							
							while(batchFlag)
							{
								synchronized (this) {
									log.info("==PRINT_JOB_ID=="+printOperatorInterfaceDetails.getId()+"==SYNCCHRO=LOOP_SIZE==>"+printedDataDetailsList.size());
									List<UniqueCodePrintedDataDetails> sendBufferList=new ArrayList<>();
									sentCount=20-queSize;
									log.info("==PRINT_JOB_ID=="+printOperatorInterfaceDetails.getId()+"==WHILE=="+sentCount);
									if(sentCount>0) 
									{
										//Based on buffer size getting codes from table
										/*sendBufferList = getCodesByPlantAndLineWithNoOfRecords(session,sentCount,currentYear,printOperatorInterfaceDetails.getCompanyCode(),printOperatorInterfaceDetails.getPlantCode(),printOperatorInterfaceDetails.getLineCode(),printOperatorInterfaceDetails.getId());
											if(sendBufferList.size()<=sentCount && sendBufferList.size()>0)
												sentCount=sendBufferList.size();
										*/
										
										if(printedDataDetailsList.size()<=sentCount && printedDataDetailsList.size()>0)
											sentCount=printedDataDetailsList.size();
										
										sendBufferList = printedDataDetailsList.subList(0, sentCount);
										log.info("==PRINT_JOB_ID=="+printOperatorInterfaceDetails.getId()+"==sendBufferList.size==>"+sendBufferList.size());
									}
									if(sendBufferList.size()>0)
									{
										log.info("==PRINT_JOB_ID=="+printOperatorInterfaceDetails.getId()+"==LOOP_START");
										String mfgDt = printOperatorInterfaceDetails.getManufactureDate().format(YYMMDD_FORMATTER);
										String expDt = printOperatorInterfaceDetails.getExpiryDate().format(YYMMDD_FORMATTER);
										String urlPrfix=appConfig.getProperty("URL_PREFIX")+"/10/"+printOperatorInterfaceDetails.getBatchNumber()+"/21/";
										for(UniqueCodePrintedDataDetails qrUid:sendBufferList)
										{
											sb = new StringBuffer();
											Long cnt = getPrintedCodesCountByUId(session,qrUid.getUidCode());
											log.info("==PRINT_JOB_ID=="+printOperatorInterfaceDetails.getId()+"==COUNT==>"+cnt);
											if(cnt==0)
											{
												log.info("==PRINT_JOB_ID==>"+printOperatorInterfaceDetails.getId()+"==COUNT==>"+cnt+"==>"+qrUid.getSerialNumber());
												log.info("==PRINT_JOB_ID=PrintThreadServiceWorking.isStopThreadVariable()=>"+PrintThreadServiceDominoPrinter.isStopThreadVariable()+"flag=>"+flag);
												
												if(!PrintThreadServiceDominoPrinter.isStopThreadVariable() && flag)
												{
													printedCodesHistory = new PrintedCodesHistory();
													printedCodesHistory.setCreatedOn(LocalDateTime.now());
													printedCodesHistory.setPrintJobId(printOperatorInterfaceDetails.getId().toString());
													printedCodesHistory.setUidCode(qrUid.getUidCode());
													printedCodesHistoryRepository.save(printedCodesHistory);
												
													printedCodes = new PrintedCodes();
													printedCodes.setCreatedOn(LocalDateTime.now());
													printedCodes.setPrintJobId(printOperatorInterfaceDetails.getId().toString());
													printedCodes.setUidCode(qrUid.getUidCode());
													printedCodesRepository.save(printedCodes);
													
													qrUid.setPrintJobMaster(printOperatorInterfaceDetails);
													qrUid.setProductMaster(printOperatorInterfaceDetails.getProductMaster());
													qrUid.setActive(true);
													qrUid.setProductName(printOperatorInterfaceDetails.getProductName());
													qrUid.setPackSize(printOperatorInterfaceDetails.getPackSize());
													qrUid.setPackUnit(printOperatorInterfaceDetails.getPackUnit());
//													qrUid.setGtinNumber(printOperatorInterfaceDetails.getGtinNumber());
													qrUid.setBatchNumber(printOperatorInterfaceDetails.getBatchNumber());
													qrUid.setManufactureDate(printOperatorInterfaceDetails.getManufactureDate());
													qrUid.setExpiryDate(printOperatorInterfaceDetails.getExpiryDate());
													qrUid.setMrp(printOperatorInterfaceDetails.getMrp());
													qrUid.setYearofUsage(currentYear);
													qrUid.setPrintedOn(LocalDateTime.now());
													qrUid.setUserId(userId);
													String url = urlPrfix+qrUid.getSerialNumber()+"?11="+mfgDt+"&17="+expDt+"&91="+qrUid.getUidCode();
													qrUid.setUrl(url);
													qrUid.setStatus("Printed");
													qrUid.setUsedDate(LocalDateTime.now());
													qrUid.setUsed(true);
													qrUid.setUnitPrice(printOperatorInterfaceDetails.getUnitPrice());
													
													log.info("IS_SHORT_URL---->"+printOperatorInterfaceDetails.getUseShortUrl());
													qrUid.setUseShortUrl(printOperatorInterfaceDetails.getUseShortUrl());
													if(printOperatorInterfaceDetails.getUseShortUrl().equalsIgnoreCase("YES")) {
														//qrUid.setShortUrl(appConfig.getProperty("SHORT_URL_PREFIX")+qrUid.getUidCode()+"/"+qrUid.getPlantNumber());
														//qrUid.setShortUrl(appConfig.getProperty("URL_PREFIX")+qrUid.getGtinNumber()+"/91/"+qrUid.getUidCode()+"/"+qrUid.getPlantNumber());
														
														qrUid.setShortUrl(appConfig.getProperty("URL_PREFIX")+qrUid.getGtinNumber()+"/21/"+qrUid.getUidCode());	

														sb.append(SOH+"FillSerialVar="+STX+"BNO_VAR"+ETX+","+STX+batchNo+ETX+ETB);
														 sb.append(SOH+"FillSerialVar="+STX+"MFG_VAR"+ETX+","+STX+mfgDt1+ETX+ETB);
														 sb.append(SOH+"FillSerialVar="+STX+"EXP_VAR"+ETX+","+STX+expDt1+ETX+ETB);
														 sb.append(SOH+"FillSerialVar="+STX+"MRP_VAR"+ETX+","+STX+""+mrp+ETX+ETB);
														 sb.append(SOH+"FillSerialVar="+STX+"UNIT_PRICE_VAR"+ETX+","+STX+unitPrice+ETX+ETB);
														 sb.append(SOH+"FillSerialVar="+STX+"QR_URL_VAR"+ETX+","+STX+qrUid.getShortUrl()+ETX+ETB);
														 sendCommandNoResponse(sb.toString(),socket);
													}
													else {
														sb.append(SOH+"FillSerialVar="+STX+"BNO_VAR"+ETX+","+STX+batchNo+ETX+ETB);
														 sb.append(SOH+"FillSerialVar="+STX+"MFG_VAR"+ETX+","+STX+mfgDt1+ETX+ETB);
														 sb.append(SOH+"FillSerialVar="+STX+"EXP_VAR"+ETX+","+STX+expDt1+ETX+ETB);
														 sb.append(SOH+"FillSerialVar="+STX+"MRP_VAR"+ETX+","+STX+""+mrp+ETX+ETB);
														 sb.append(SOH+"FillSerialVar="+STX+"UNIT_PRICE_VAR"+ETX+","+STX+unitPrice+ETX+ETB);
														 sb.append(SOH+"FillSerialVar="+STX+"QR_URL_VAR"+ETX+","+STX+url+ETX+ETB);
														 sendCommandNoResponse(sb.toString(),socket);
													}
													
													uniqueCodePrintedDataDetailsRepository.save(qrUid);
													
													count++;
													log.info("==PRINT_JOB_ID==>"+printOperatorInterfaceDetails.getId()+"==QR_CODE==>"+url);
													printedCodesHistory.setPrintedUrl(url);
													printedCodesHistoryRepository.save(printedCodesHistory);
													//Thread.sleep(300);
													log.info("==PRINT_JOB_ID==>"+printOperatorInterfaceDetails.getId()+"==JDI_LOOP_COUNT==>"+count);
												}
												else {
													batchFlag=false;
													stopThreadVariable=true;
													PrintThreadServiceDominoPrinter.setStopThreadVariable(true);
													PrintThreadServiceDominoPrinter.setWhileLoopFlag(1);
													break;
												}
											}
											else {
												duplicatePrintCodes = new DuplicatePrintCodes();
												duplicatePrintCodes.setCreatedOn(LocalDateTime.now());
												duplicatePrintCodes.setPrintJobId(printOperatorInterfaceDetails.getId().toString());
												duplicatePrintCodes.setUidCode(qrUid.getUidCode());
												duplicatePrintCodesRepository.save(duplicatePrintCodes);
												
												//qrUid.setUsed(true);
												//printProductQRCodeDetailsManager.saveOrUpdate(qrUid);
												
												log.info("==ALREADY_PRINTED_UID==>"+qrUid.getUidCode());
												
								                stopThreadVariable=true;
								                PrintThreadServiceDominoPrinter.setWhileLoopFlag(1);
								                PrintThreadServiceDominoPrinter.setStopThreadVariable(true);
								                
								                String updateQuery = " update UNIQUE_CODE_PRINTED_DATA_DETAILS set USED=1 where UID_CODE='"+qrUid.getUidCode()+"' and USED=0 ";
												executeSqlQuery(session, updateQuery);
												log.info("==PRINT_JOB_ID==>"+printOperatorInterfaceDetails.getId()+"==AFTER_USED_UPDATED==");
								                
												break;
											}
										}
										log.info("==PRINT_JOB_ID=="+printOperatorInterfaceDetails.getId()+"==LOOP_END");
										printOperatorInterfaceDetails.setPrintingStatus("Started");
										Long alreadyPrintedQty = getPrintCountByPrintJobId(session,printOperatorInterfaceDetails.getId()); 
										log.info("==PRINT_JOB_ID=="+printOperatorInterfaceDetails.getId()+"==alreadyPrintedQty==>"+alreadyPrintedQty);
										printOperatorInterfaceDetails.setNoOfSachesPrinted(alreadyPrintedQty.intValue());
										printJobMasterRepository.save(printOperatorInterfaceDetails);
									}
									
									totalSentCount=totalSentCount+sentCount;
									log.info("==PRINT_JOB_ID=="+printOperatorInterfaceDetails.getId()+"==printedDataDetailsList=SIZE="+printedDataDetailsList.size());
									log.info("==PRINT_JOB_ID=="+printOperatorInterfaceDetails.getId()+"==sendBufferList=SIZE="+sendBufferList.size());
									List<UniqueCodePrintedDataDetails> unmatchedList = (List<UniqueCodePrintedDataDetails>) CollectionUtils.removeAll(printedDataDetailsList,sendBufferList);
								    log.info("==PRINT_JOB_ID=="+printOperatorInterfaceDetails.getId()+"==unmatchedList==>"+unmatchedList.size());
								    if(unmatchedList.size()>0) {
								    	balPrints=balPrints-sentCount;
								    	if(unmatchedList.size()<=20)
								    		sentCount=unmatchedList.size();
								    	else
								    		sentCount=20;
								    }
								    printedDataDetailsList.clear();
								    printedDataDetailsList=unmatchedList;
								    log.info("==PRINT_JOB_ID=="+printOperatorInterfaceDetails.getId()+"==printedDataDetailsList==>"+printedDataDetailsList.size());
								    
									int qsz = getAvailablePrintCountsReadyToPrint(socket);
									log.info("==PRINT_JOB_ID=="+printOperatorInterfaceDetails.getId()+"==qsz==>"+qsz);
									if(qsz==0 || qsz==20)
										queSize= qsz;
									else
										queSize= qsz+1;
									
									log.info("==PRINT_JOB_ID=="+printOperatorInterfaceDetails.getId()+"==qszCmdResp==>"+queSize);
									
									if(queSize != 100) {
										if(queSize==0 && totalSentCount==noOfPrints)
											batchFlag=false;
										else if(queSize==0 && totalSentCount<noOfPrints)
											sentCount=20-queSize;
										else if(queSize>0 && totalSentCount<noOfPrints)
											sentCount=20-queSize;
										else if(totalSentCount==noOfPrints)
											batchFlag=false;
									}else {
										batchFlag = false;
									}
									log.info("==PRINT_JOB_ID=="+printOperatorInterfaceDetails.getId()+"==sentCount==>"+sentCount);
									log.info("==PRINT_JOB_ID=="+printOperatorInterfaceDetails.getId()+"==totalSentCount==>"+totalSentCount);
								}//Synchronized end
								
								//break while loop
								log.info("===PRINT_JOB_ID==>=PrintThreadServiceWorking.getWhileLoopFlag()==>"+printOperatorInterfaceDetails.getId()+"=="+PrintThreadServiceDominoPrinter.getWhileLoopFlag());
								if(PrintThreadServiceDominoPrinter.getWhileLoopFlag()==1) {
									break;
								}
								
							}//While end
							log.info("==PRINT_JOB_ID=="+printOperatorInterfaceDetails.getId()+"==totalSentCount==count=>"+totalSentCount+"=="+count);
							
							if (PrintThreadServiceDominoPrinter.isStopThreadVariable()) {
								printOperatorInterfaceDetails.setPrintingStatus("Cancelled");
								printJobMasterRepository.save(printOperatorInterfaceDetails);
							} else if (totalSentCount == count) {
								printOperatorInterfaceDetails.setPrintingStatus("Completed");
								printJobMasterRepository.save(printOperatorInterfaceDetails);
							} else {
								printOperatorInterfaceDetails.setPrintingStatus("Partial");
								printJobMasterRepository.save(printOperatorInterfaceDetails);
							}
							socket.close();
							stopThreadVariable=true;
							PrintThreadServiceDominoPrinter.setWhileLoopFlag(1);
							PrintThreadServiceDominoPrinter.setStopThreadVariable(true);
					}//Synchronized close
						
				} 
				catch (Exception e)
				{ 
					PrintThreadServiceDominoPrinter.setWhileLoopFlag(1);
					stopThreadVariable=true;
					PrintThreadServiceDominoPrinter.setStopThreadVariable(true);
	                log.info(""+e.getStackTrace(),e);
	            }
			}

			public long getPrintCountByPrintJobId(Session session, Long printJobMasterId) {
				String sql = "SELECT COUNT(*) FROM UNIQUE_CODE_PRINTED_DATA_DETAILS WHERE active = 1 AND PRINT_JOB_MASTER_ID = :printJobMasterId AND status = 'Printed'";
				log.info("==getPrintCountByPrintJobId==> " + sql);
				Object result = session.createNativeQuery(sql).setParameter("printJobMasterId", printJobMasterId).getSingleResult();
				return ((Number) result).longValue();
			}
			
			public static String sendCommandToPrinter(String sndCmd, Socket socket) {
				String resp = "";
				OutputStream output = null;
				try {
					BufferedReader reader = null;
					output = socket.getOutputStream();
					reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
					output.write(sndCmd.getBytes());
					String response = reader.readLine();
					resp = response.toString();
				} catch (Exception e) {
					log.info("Exec==>" + e.getStackTrace(), e);
					resp = "ERROR";
				}
				return resp;
			}

			@SuppressWarnings("unused")
			public static String sendCommandNoResponse(String sndCmd, Socket socket) {
				String resp = "";
				OutputStream output = null;
				try {
					BufferedReader reader = null;
					output = socket.getOutputStream();
					reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
					output.write(sndCmd.getBytes());
					resp = "Success";
				} catch (Exception e) {
					log.info("Exec==>" + e.getStackTrace(), e);
					resp = "ERROR";
				}
				return resp;
			}

			public Object findObject(Session session, String queryString, Object... values) throws DataAccessException {Query<?> queryObject = session.createQuery(queryString);
				queryObject.setCacheable(true);
				if (values != null) {
					for (int i = 0; i < values.length; i++) {
						// Hibernate 5.2+ positional params start at 0 → ?0, ?1, ?2
						queryObject.setParameter(i, values[i]);
					}
				}
				List<?> results = queryObject.list();
				return results.isEmpty() ? null : results.get(0);
			}

			@SuppressWarnings("rawtypes")
			public List find(Session session, String queryString, Object... values) throws DataAccessException {
				Query queryObject = session.createQuery(queryString);
				queryObject.setCacheable(true);
				if (values != null) {
					for (int i = 0; i < values.length; i++) {
						queryObject.setParameter(i, values[i]);
					}
				}
				return queryObject.list();
			}

			@SuppressWarnings("rawtypes")
			public List findBySql(Session session, String queryString, Object... values) throws DataAccessException {
				NativeQuery queryObject = session.createNativeQuery(queryString);
				queryObject.setCacheable(true);
				if (values != null) {
					for (int i = 0; i < values.length; i++) {
						// In Hibernate 5.2+ positional params are 0-based → ?0, ?1, ?2
						queryObject.setParameter(i, values[i]);
					}
				}
				return queryObject.getResultList(); // instead of .list()
			}

			public static int getAvailablePrintCountsReadyToPrint(Socket socket) {
				int size = 0;
				if (socket != null) {
					if (socket.isConnected()) {
						OutputStream output = null;
						boolean flag = true;
						String sndCmd = SOH + "PollSerialVar=" + STX + "QR_URL_VAR" + ETX + ETB;
						while (flag) {
							try {
								BufferedReader reader = null;
								output = socket.getOutputStream();
								reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
								output.write(sndCmd.getBytes());
								// Thread.sleep(1000);
								String response = reader.readLine();// RESULT GETBUFFERSTATUS 3
								log.info("PollSerialVar_CMD_RESP==>" + response);
								if (response.contains("AnswerSerialVar")) {
									log.info("PollSerialVar_SIZE=>" + response.split("=")[1]);
									String pp = response.split("=")[1].split(",")[2];
									log.info("bufferSize =====" + (pp.substring(0, pp.length() - 1)));
									size = Integer.parseInt(pp.substring(0, pp.length() - 1));
									log.info("PollSerialVar_size==>" + size);
									flag = false;
								} else {
									flag = true;
								}
							} catch (Exception e) {
								log.info("Exec==>" + e.getStackTrace(), e);
								flag = false;
								size = -1;
							}
						}
						log.info("RETURN_BUFFER_SIZE==>" + size);
					}
				}
				return size;

			}

			@SuppressWarnings({ "rawtypes", "deprecation", "unchecked" })
			public String createQRCode(String qrCodeData, String filePath) {
				try {
					String charset = "UTF-8";
					Map hintMap = new HashMap();
					int qrCodeheight = 200;
					int qrCodewidth = 200;
					BitMatrix matrix = new MultiFormatWriter().encode(new String(qrCodeData.getBytes(charset), charset),BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight, hintMap);
					MatrixToImageWriter.writeToFile(matrix, filePath.substring(filePath.lastIndexOf('.') + 1),new File(filePath));
				} catch (Exception e) {
					log.info("" + e.getStackTrace(), e);
				}
				return filePath;
			}

			@SuppressWarnings("unused")
			public String createTemplateWithQRCodeAndValues(String pdfFileName, String qrcodeImgFilePath,
					String batchNo, String mfgDt, String expDt, String mrp) {
				Document document = new Document();
				try {
					PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFileName));
					document.open();

					// document.add(new Paragraph("A Hello World PDF document."));
					Rectangle one = new Rectangle(50, 50);
					document.setPageSize(one);
					document.addAuthor("Empover");
					document.addCreationDate();
					document.addCreator("empover.com");
					document.addTitle("Print Operator Interface");
					document.addSubject("QR Code File");

					Image image1 = Image.getInstance(qrcodeImgFilePath);
					image1.setAbsolutePosition(100f, 550f);
					image1.scaleAbsolute(100, 100);

					PdfPTable table = new PdfPTable(2); // 2 columns.

					table.setWidthPercentage(50); // Width 100%
					table.setSpacingBefore(100F); // Space before table
					table.setSpacingAfter(10f); // Space after table

					// Set Column widths
					float[] columnWidths = new float[] { 25f, 50f };
					table.setWidths(columnWidths);

					Font regular = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);
					Font bold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
					Phrase p = new Phrase("" + batchNo + "\n" + mfgDt + "\n" + expDt, bold);
					// p.add(new Chunk(CC_CUST_NAME, regular));
					PdfPCell cell1 = new PdfPCell(p);
					cell1.setBorderColor(BaseColor.BLACK);
					cell1.setPaddingLeft(10);
					cell1.setMinimumHeight(90f);
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);

					PdfPCell cell2 = new PdfPCell(new Paragraph(""));
					cell2.setBorderColor(BaseColor.BLACK);
					cell2.setPaddingLeft(10);
					cell1.setMinimumHeight(90f);
					cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);

					Phrase mr = new Phrase(mrp, bold);
					PdfPCell cell3 = new PdfPCell(mr);
					cell3.setBorderColor(BaseColor.BLACK);
					cell3.setPaddingLeft(10);
					cell3.setMinimumHeight(90f);
					cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);

					PdfPCell cell4 = new PdfPCell();
					cell4.setBorderColor(BaseColor.BLACK);
					cell4.setPaddingLeft(10);
					cell4.setMinimumHeight(90f);
					cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell4.addElement(image1);

					// To avoid having the cell border and the content overlap, if you are having
					// thick cell borders
					// cell1.setUseBorderPadding(true);
					// cell2.setUseBorderPadding(true);

					table.addCell(cell1);
					table.addCell(cell2);
					table.addCell(cell3);
					table.addCell(cell4);

					document.add(table);

					document.close();
					writer.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return pdfFileName;
			}

			public String createTextFileWithQRCodeAndValues(String txtFileName, String qrcodeImgFilePath,
					String batchNo, String mfgDt, String expDt, String mrp) {
				try {
					// Whatever the file path is.
					File txtFile = new File(txtFileName);
					FileOutputStream is = new FileOutputStream(txtFile);
					OutputStreamWriter osw = new OutputStreamWriter(is);
					Writer w = new BufferedWriter(osw);
					w.write("Batch No : " + batchNo);
					w.write("\nMfg Date : " + mfgDt);
					w.write("\nExp Date : " + expDt);
					w.write("\nMRP : " + mrp);
					w.close();
				} catch (IOException e) {
					log.info("" + e.getStackTrace(), e);
				}
				return txtFileName;
			}

			public long getPrintedCodesCountByUId(Session session, String uidCode) {
				String sql = "SELECT COUNT(*) FROM PRINTED_CODES WHERE UID_CODE = :uidCode";
				log.info("==getPrintedCodesCountByUId==> " + sql);
				return ((Number) session.createNativeQuery(sql).setParameter("uidCode", uidCode).uniqueResult()).longValue();
			}

			@SuppressWarnings({ "unchecked", "deprecation" })
			public List<UniqueCodePrintedDataDetails> getCodesByPlantAndLineWithNoOfRecords(Session session,int topRecords,int year,String companyCode,String plantCode,String lineNumber,long jobId) {
				try {
					String sql=" select top "+topRecords+" UID_CODE as uidCode,SERIAL_NUMBER as serialNumber from UNIQUE_CODE_PRINTED_DATA_DETAILS where USED='False' and CODES_YEAR="+year+" and CUSTOMER_CODE='"+companyCode+"' and PLANT_NUMBER='"+plantCode+"' and LINE_NUMBER='"+lineNumber+"'  order by SERIAL_NUMBER asc  ";
		   			log.info("==PRINTJOBID=="+jobId+"==CODES_QUERY=SESSION=>"+sql);
		   	        NativeQuery<UniqueCodePrintedDataDetails> sqlQuery = session.createNativeQuery(sql);
					sqlQuery.addScalar("uidCode", StandardBasicTypes.STRING);
					sqlQuery.addScalar("serialNumber", StandardBasicTypes.LONG);
			        sqlQuery.setResultTransformer(Transformers.aliasToBean(UniqueCodePrintedDataDetails.class));
			        return sqlQuery.list();
			    } catch (Exception e) {
			        log.error("Error fetching codes", e);
			        return new ArrayList<>();
			    }
			}

			@SuppressWarnings("unchecked")
			public List<UniqueCodePrintedDataDetails> getCodesByPlantAndLineWithNoOfRecordsManager(Session session,int topRecords, int year, String companyCode, String plantCode, String lineNumber, long jobId) {
				try {
					String sql=" select top "+topRecords+" * from UNIQUE_CODE_PRINTED_DATA_DETAILS where USED='False' and CODES_YEAR="+year+" and CUSTOMER_CODE='"+companyCode+"' and PLANT_NUMBER='"+plantCode+"' and LINE_NUMBER='"+lineNumber+"'  order by SERIAL_NUMBER asc  ";
					log.info("==PRINTJOBID==" + jobId + "==CODES_QUERY_MANAGER=>" + sql);
					List<UniqueCodePrintedDataDetails> codesList = session.createNativeQuery(sql).setResultTransformer(Transformers.aliasToBean(UniqueCodePrintedDataDetails.class)).list();
					return codesList;
				} catch (Exception e) {
					log.info(e.getMessage());
					return new ArrayList<>();
				}
			}

			public void executeSqlQuery(Session session, String sql) {
			    org.hibernate.Transaction tx = null;
			    try {
			        tx = session.beginTransaction();
			        session.createNativeQuery(sql).executeUpdate();
			        tx.commit();
			    } catch (HibernateException e) {
			        if (tx != null) tx.rollback();
			        log.error("Error executing SQL: " + sql, e);
			    }
			}

			public Socket conectToNetWorkPrinter() {
				Socket socket = null;
				try {
					socket = new Socket(appConfig.getProperty("PRINTER_IP"),Integer.parseInt(appConfig.getProperty("PRINTER_PORT")));
					socket.setSoTimeout(5000);
				} catch (Exception e) {
					System.out.println("ConnectToPrinter==>" + e.toString());
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					// socket=conectVideojetToPrinter();
				}

				if (socket != null)
					log.info("==SOCKET_CONNECTED==");
				else
					log.info("==SOCKET_NOT_CONNECTED==");

				return socket;
			}
		}
