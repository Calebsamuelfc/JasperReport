package com.itssc.jasperreport.service.api;

import com.itssc.jasperreport.dto.request.TransactionReceiptRequestDTO;
import com.itssc.jasperreport.dto.response.ServiceResponse;

public interface TransactionReceiptService {
    ServiceResponse downloadSingleTransactionReceipt(TransactionReceiptRequestDTO transactionReceiptRequestDTO);
}
