package com.itssc.jasperreport.service.api;

import com.itssc.jasperreport.dto.request.CombinedStatementRequestDTO;
import com.itssc.jasperreport.dto.response.ServiceResponse;

public interface CombinedStatementReceiptService {
    ServiceResponse downloadCombinedStatementReceipt(CombinedStatementRequestDTO combinedStatementRequestDTO);
}
