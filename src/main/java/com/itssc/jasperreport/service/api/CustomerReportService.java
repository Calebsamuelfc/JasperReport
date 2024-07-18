package com.itssc.jasperreport.service.api;

import com.itssc.jasperreport.dto.request.CombinedStatementRequestDTO;
import com.itssc.jasperreport.dto.response.ServiceResponse;

public interface CustomerReportService {
    ServiceResponse downloadCustomerReport(CombinedStatementRequestDTO combinedStatementRequestDTO);


}
