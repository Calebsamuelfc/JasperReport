package com.itssc.jasperreport.service.api;

import com.itssc.jasperreport.dto.request.CombinedStatementRequestDTO;
import com.itssc.jasperreport.dto.request.TransactionReceiptRequestDTO;
import com.itssc.jasperreport.dto.response.MobileServiceResponse;
import com.itssc.jasperreport.dto.response.ServiceResponse;

public interface TransactionReceiptService {
    ServiceResponse downloadSingleTransactionReceipt(TransactionReceiptRequestDTO transactionReceiptRequestDTO);
    MobileServiceResponse downloadSingleTransactionReceiptMobile (TransactionReceiptRequestDTO transactionReceiptRequestDTO);

}
