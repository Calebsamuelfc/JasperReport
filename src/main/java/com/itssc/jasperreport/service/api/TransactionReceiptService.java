package com.itssc.jasperreport.service.api;

import com.itssc.jasperreport.dto.request.TransactionReceiptRequestDTO;
import com.itssc.jasperreport.dto.response.RibResponse;

public interface TransactionReceiptService {
    RibResponse downloadSingleTransactionReceipt(TransactionReceiptRequestDTO transactionReceiptRequestDTO);
}
