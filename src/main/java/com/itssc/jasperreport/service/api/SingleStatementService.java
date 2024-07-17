package com.itssc.jasperreport.service.api;

import com.itssc.jasperreport.dto.request.SingleStatementRequestDTO;
import com.itssc.jasperreport.dto.response.MobileServiceResponse;
import com.itssc.jasperreport.dto.response.ServiceResponse;

public interface SingleStatementService {
    ServiceResponse downloadSingleStatement(SingleStatementRequestDTO SingleStatementRequestDTO);
    MobileServiceResponse downloadSingleStatementMobile(SingleStatementRequestDTO SingleStatementRequestDTO);
}
