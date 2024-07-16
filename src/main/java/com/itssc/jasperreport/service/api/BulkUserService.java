package com.itssc.jasperreport.service.api;

import com.itssc.jasperreport.dto.request.CombinedStatementRequestDTO;
import com.itssc.jasperreport.dto.response.ServiceResponse;

public interface BulkUserService {
    ServiceResponse downloadBulkUser(CombinedStatementRequestDTO combinedStatementRequestDTO);
    ServiceResponse downloadBulkUserCsv(CombinedStatementRequestDTO combinedStatementRequestDTO);
    ServiceResponse downloadBulkUserExcel(CombinedStatementRequestDTO combinedStatementRequestDTO);

}
