package com.nsl.operatorInterface.service;

import java.util.List;
import com.nsl.operatorInterface.entity.PrinterMaster;
import com.nsl.operatorInterface.request.PrinterMasterRequest;

public interface PrinterMasterService {

	PrinterMaster addPrinter(PrinterMasterRequest request);

	PrinterMaster updatePrinter(Long id, PrinterMasterRequest request);

	List<PrinterMaster> getAllActivePrinters();

	List<PrinterMaster> getAllPrinters();

	PrinterMaster getPrinterById(Long id);

	PrinterMaster getPrinterByLineNumber(String lineNumber);

	PrinterMaster deactivatePrinter(Long id);

}
