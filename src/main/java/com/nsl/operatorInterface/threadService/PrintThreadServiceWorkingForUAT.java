package com.nsl.operatorInterface.threadService;

import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
public class PrintThreadServiceWorkingForUAT implements Runnable{

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

//	protected HibernateDao hibernateDao;
//	private Properties appConfig;
//	private PrintedDataDetailsManager printProductQRCodeDetailsManager;
//	private List<PrintedDataDetails> printedDataDetailsList;
//	private PrintJobMaster printOperatorInterfaceDetails;
//	private PrintJobMasterManager printJobMasterManager;
//	@SuppressWarnings("unused")
//	private Socket socket;
//	private String userId;
//	private PrintedCodesManager printedCodesManager;
//	private PrintedCodes printedCodes;
//	private PrintedCodesHistory printedCodesHistory;
//	private PrintedCodesHistoryManager printedCodesHistoryManager;
//	private DuplicatePrintCodes duplicatePrintCodes;
//	private DuplicatePrintCodesManager duplicatePrintCodesManager;  
//	private int currentYear;
//	private QRCodeService qrCodeService;
//	private static boolean stopThreadVariable = false;
//	private static int whileLoopFlag = 0;

	public PrintThreadServiceWorkingForUAT() {

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

	public UniqueCodePrintedDataDetailsRepository getUniqueCodePrintedDataDetailsRepository() {
		return uniqueCodePrintedDataDetailsRepository;
	}

	public void setUniqueCodePrintedDataDetailsRepository(
			UniqueCodePrintedDataDetailsRepository uniqueCodePrintedDataDetailsRepository) {
		this.uniqueCodePrintedDataDetailsRepository = uniqueCodePrintedDataDetailsRepository;
	}

	public List<UniqueCodePrintedDataDetails> getPrintedDataDetailsList() {
		return printedDataDetailsList;
	}

	public void setPrintedDataDetailsList(List<UniqueCodePrintedDataDetails> printedDataDetailsList) {
		this.printedDataDetailsList = printedDataDetailsList;
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
		PrintThreadServiceWorkingForUAT.socket = socket;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public PrintedCodesRepository getPrintedCodesRepository() {
		return printedCodesRepository;
	}

	public void setPrintedCodesRepository(PrintedCodesRepository printedCodesRepository) {
		this.printedCodesRepository = printedCodesRepository;
	}

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

	public PrintedCodesHistoryRepository getPrintedCodesHistoryRepository() {
		return printedCodesHistoryRepository;
	}

	public void setPrintedCodesHistoryRepository(PrintedCodesHistoryRepository printedCodesHistoryRepository) {
		this.printedCodesHistoryRepository = printedCodesHistoryRepository;
	}

	public DuplicatePrintCodes getDuplicatePrintCodes() {
		return duplicatePrintCodes;
	}

	public void setDuplicatePrintCodes(DuplicatePrintCodes duplicatePrintCodes) {
		this.duplicatePrintCodes = duplicatePrintCodes;
	}

	public DuplicatePrintCodesRepository getDuplicatePrintCodesRepository() {
		return duplicatePrintCodesRepository;
	}

	public void setDuplicatePrintCodesRepository(DuplicatePrintCodesRepository duplicatePrintCodesRepository) {
		this.duplicatePrintCodesRepository = duplicatePrintCodesRepository;
	}

	public int getCurrentYear() {
		return currentYear;
	}

	public void setCurrentYear(int currentYear) {
		this.currentYear = currentYear;
	}

	public QRCodeService getQrCodeService() {
		return qrCodeService;
	}

	public void setQrCodeService(QRCodeService qrCodeService) {
		this.qrCodeService = qrCodeService;
	}

	public static boolean isStopThreadVariable() {
		return stopThreadVariable;
	}

	public static void setStopThreadVariable(boolean stopThreadVariable) {
		PrintThreadServiceWorkingForUAT.stopThreadVariable = stopThreadVariable;
	}

	public static int getWhileLoopFlag() {
		return whileLoopFlag;
	}

	public static void setWhileLoopFlag(int whileLoopFlag) {
		PrintThreadServiceWorkingForUAT.whileLoopFlag = whileLoopFlag;
	}

	public void run() {
		Session session = hibernateDao.openSession();
		try {
					synchronized (session) {
							//String batchNo=printOperatorInterfaceDetails.getBatchNumber();
							//String mfgDt1=DateUtils.getdatefromTimeStampAsddMMMyyyy(printOperatorInterfaceDetails.getManufactureDate()).toUpperCase();
							//String expDt1=DateUtils.getdatefromTimeStampAsddMMMyyyy(printOperatorInterfaceDetails.getExpiryDate()).toUpperCase();
							//String mrp= String.format("%.2f",printOperatorInterfaceDetails.getMrp().doubleValue());
							//String templateName = printOperatorInterfaceDetails.getSelectedTemplateName();
							log.info("=UAT=PRINT_JOB_ID=="+printOperatorInterfaceDetails.getId()+"==THREAD_STARTED");
							//String tempSelCmd="SEL|"+templateName+"|\r";
							//String dataUpdateCmd="SLA|"+templateName+"|BNO_VAR="+batchNo+"|MFG_VAR="+mfgDt1+"|EXP_VAR="+expDt1+"|MRP_VAR="+mrp+"|URL_VAR=dummyurl|\r";
							boolean flag=true;

							int count = 0;
							int noOfPrints = printedDataDetailsList.size();
							int balPrints = noOfPrints;
							int sentCount = 0;
							if (noOfPrints > 20)
								sentCount = 20;
							else
								sentCount = noOfPrints;

							int totalSentCount = 0;
							boolean batchFlag = true;

							while (batchFlag) {
								synchronized (this) {
									log.info("=UAT=PRINT_JOB_ID==" + printOperatorInterfaceDetails.getId()+ "==SYNCCHRO=LOOP_SIZE==>" + printedDataDetailsList.size());
									List<UniqueCodePrintedDataDetails> sendBufferList = new ArrayList<>();
									log.info("=UAT=PRINT_JOB_ID==" + printOperatorInterfaceDetails.getId() + "==WHILE=="+ sentCount);
									if (sentCount > 0) {
										//Based on buffer size getting codes from table
										/*sendBufferList = getCodesByPlantAndLineWithNoOfRecords(session,sentCount,currentYear,printOperatorInterfaceDetails.getCompanyCode(),printOperatorInterfaceDetails.getPlantCode(),printOperatorInterfaceDetails.getLineCode(),printOperatorInterfaceDetails.getId());
											if(sendBufferList.size()<=sentCount && sendBufferList.size()>0)
												sentCount=sendBufferList.size();
										*/

										if (printedDataDetailsList.size() <= sentCount&& printedDataDetailsList.size() > 0)
											sentCount = printedDataDetailsList.size();

										sendBufferList = printedDataDetailsList.subList(0, sentCount);
										log.info("=UAT=PRINT_JOB_ID==" + printOperatorInterfaceDetails.getId()+ "==sendBufferList.size==>" + sendBufferList.size());
									}
									if (sendBufferList.size() > 0) {
										log.info("=UAT=PRINT_JOB_ID=="+printOperatorInterfaceDetails.getId()+"==LOOP_START");
										String mfgDt = printOperatorInterfaceDetails.getManufactureDate().format(YYMMDD_FORMATTER);
										String expDt = printOperatorInterfaceDetails.getExpiryDate().format(YYMMDD_FORMATTER);
										String urlPrfix=appConfig.getProperty("URL_PREFIX")+"/10/"+printOperatorInterfaceDetails.getBatchNumber()+"/21/";
										for(UniqueCodePrintedDataDetails qrUid:sendBufferList)
										{
											Long cnt = getPrintedCodesCountByUId(session,qrUid.getUidCode());
											log.info("=UAT=PRINT_JOB_ID=="+printOperatorInterfaceDetails.getId()+"==COUNT==>"+cnt);
											if(cnt==0)
											{
												log.info("=UAT=PRINT_JOB_ID==>"+printOperatorInterfaceDetails.getId()+"==COUNT==>"+cnt+"==>"+qrUid.getSerialNumber());
												log.info("=UAT=PRINT_JOB_ID=PrintThreadServiceWorking.isStopThreadVariable()=>"+PrintThreadServiceWorkingForUAT.isStopThreadVariable());
												
												if(!PrintThreadServiceWorkingForUAT.isStopThreadVariable() && flag)
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
													qrUid.setUnitPrice(printOperatorInterfaceDetails.getUnitPrice());
													//String url = appConfig.getProperty("URL_PREFIX")+printOperatorInterfaceDetails.getGtinNumber()+"/10/"+printOperatorInterfaceDetails.getBatchNumber()+"/21/"+qrUid.getSerialNumber()+"?11="+mfgDt+"&17="+expDt+"&91="+qrUid.getUidCode();
													String url = urlPrfix+qrUid.getSerialNumber()+"?11="+mfgDt+"&17="+expDt+"&91="+qrUid.getUidCode();
													qrUid.setUrl(url);
													qrUid.setStatus("Printed");
													qrUid.setUsedDate(LocalDateTime.now());
													qrUid.setUsed(true);
													
													log.info("IS_SHORT_URL---->"+printOperatorInterfaceDetails.getUseShortUrl());
													qrUid.setUseShortUrl(printOperatorInterfaceDetails.getUseShortUrl());
													if(printOperatorInterfaceDetails.getUseShortUrl().equalsIgnoreCase("YES")) {
														qrUid.setShortUrl(appConfig.getProperty("URL_PREFIX")+qrUid.getGtinNumber()+"/91/"+qrUid.getUidCode()+"/"+qrUid.getPlantNumber());
														//qrUid.setShortUrl(appConfig.getProperty("SHORT_URL_PREFIX")+qrUid.getUidCode()+"/"+qrUid.getPlantNumber());
													}
													
													uniqueCodePrintedDataDetailsRepository.save(qrUid);
													//String urlUpdateCmd="JDI|1|URL_VAR="+url+"|\r";   //Url value will not print duplicate in command|
													//String updateResp= sendCommandToPrinter(urlUpdateCmd,socket);
													count++;
													log.info("=UAT=PRINT_JOB_ID==>"+printOperatorInterfaceDetails.getId()+"==QR_CODE==>"+url);
													printedCodesHistory.setPrintedUrl(url);
													printedCodesHistoryRepository.save(printedCodesHistory);
													//Thread.sleep(300);
													log.info("=UAT=PRINT_JOB_ID==>"+printOperatorInterfaceDetails.getId()+"==JDI_LOOP_COUNT==>"+count);
												}
												else {
													batchFlag=false;
													stopThreadVariable=true;
													PrintThreadServiceWorkingForUAT.setStopThreadVariable(true);
													PrintThreadServiceWorkingForUAT.setWhileLoopFlag(1);
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
												
												log.info("=UAT=ALREADY_PRINTED_UID==>"+qrUid.getUidCode());
												
								                stopThreadVariable=true;
								                PrintThreadServiceWorkingForUAT.setWhileLoopFlag(1);
								                PrintThreadServiceWorkingForUAT.setStopThreadVariable(true);
								                
								                String updateQuery = " update UNIQUE_CODE_PRINTED_DATA_DETAILS set USED=1 where UID_CODE='"+qrUid.getUidCode()+"' and USED=0 ";
												executeSqlQuery(session, updateQuery);
												log.info("=UAT=PRINT_JOB_ID==>"+printOperatorInterfaceDetails.getId()+"==AFTER_USED_UPDATED==");
												break;
											}
										}
										log.info("=UAT=PRINT_JOB_ID=="+printOperatorInterfaceDetails.getId()+"==LOOP_END");
										printOperatorInterfaceDetails.setPrintingStatus("Started");
										Long alreadyPrintedQty = getPrintCountByPrintJobId(session,printOperatorInterfaceDetails.getId()); 
										log.info("==PRINT_JOB_ID=="+printOperatorInterfaceDetails.getId()+"==alreadyPrintedQty==>"+alreadyPrintedQty);
										printOperatorInterfaceDetails.setNoOfSachesPrinted(alreadyPrintedQty.intValue());
										printJobMasterRepository.save(printOperatorInterfaceDetails);
									}
									
									totalSentCount=totalSentCount+sentCount;
									log.info("=UAT=PRINT_JOB_ID=="+printOperatorInterfaceDetails.getId()+"==printedDataDetailsList=SIZE="+printedDataDetailsList.size());
									log.info("=UAT=PRINT_JOB_ID=="+printOperatorInterfaceDetails.getId()+"==sendBufferList=SIZE="+sendBufferList.size());
									List<UniqueCodePrintedDataDetails> unmatchedList = (List<UniqueCodePrintedDataDetails>) CollectionUtils.removeAll(printedDataDetailsList,sendBufferList);
								    log.info("=UAT=PRINT_JOB_ID=="+printOperatorInterfaceDetails.getId()+"==unmatchedList==>"+unmatchedList.size());
								    if(unmatchedList.size()>0) {
								    	balPrints=balPrints-sentCount;
								    	if(unmatchedList.size()<=20)
								    		sentCount=unmatchedList.size();
								    	else
								    		sentCount=20;
								    }
								    printedDataDetailsList.clear();
								    printedDataDetailsList=unmatchedList;
								    log.info("=UAT=PRINT_JOB_ID=="+printOperatorInterfaceDetails.getId()+"==printedDataDetailsList==>"+printedDataDetailsList.size());
									batchFlag = false;
									log.info("=UAT=PRINT_JOB_ID=="+printOperatorInterfaceDetails.getId()+"==sentCount==>"+sentCount);
									log.info("=UAT=PRINT_JOB_ID=="+printOperatorInterfaceDetails.getId()+"==totalSentCount==>"+totalSentCount);
								}//Synchronized end
								
								//break while loop
								log.info("=UAT==PRINT_JOB_ID==>=PrintThreadServiceWorking.getWhileLoopFlag()==>"+printOperatorInterfaceDetails.getId()+"=="+PrintThreadServiceWorkingForUAT.getWhileLoopFlag());
								if(PrintThreadServiceWorkingForUAT.getWhileLoopFlag()==1) {
									break;
								}
								
							}//While end
							log.info("=UAT=PRINT_JOB_ID=="+printOperatorInterfaceDetails.getId()+"==totalSentCount==count=>"+totalSentCount+"=="+count);

							if (PrintThreadServiceWorkingForUAT.isStopThreadVariable()) {
								printOperatorInterfaceDetails.setPrintingStatus("Cancelled");
								printJobMasterRepository.save(printOperatorInterfaceDetails);
							} else if (totalSentCount == count) {
								printOperatorInterfaceDetails.setPrintingStatus("Completed");
								printJobMasterRepository.save(printOperatorInterfaceDetails);
							} else {
								printOperatorInterfaceDetails.setPrintingStatus("Partial");
								printJobMasterRepository.save(printOperatorInterfaceDetails);
							}
							stopThreadVariable=true;
							PrintThreadServiceWorkingForUAT.setWhileLoopFlag(1);
							PrintThreadServiceWorkingForUAT.setStopThreadVariable(true);
					}//Synchronized close
						
				} 
				catch (Exception e)
				{ 
					PrintThreadServiceWorkingForUAT.setWhileLoopFlag(1);
					stopThreadVariable=true;
					PrintThreadServiceWorkingForUAT.setStopThreadVariable(true);
	                log.info(""+e.getStackTrace(),e);
				}
			}

			public long getPrintCountByPrintJobId(Session session, Long printJobMasterId) {
				String sql = "SELECT COUNT(*) FROM UNIQUE_CODE_PRINTED_DATA_DETAILS WHERE active = 1 AND PRINT_JOB_MASTER_ID = :printJobMasterId AND status = 'Printed'";
				log.info("==getPrintCountByPrintJobId==> " + sql);
				Object result = session.createNativeQuery(sql).setParameter("printJobMasterId", printJobMasterId).getSingleResult();
				return ((Number) result).longValue();
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

	}
