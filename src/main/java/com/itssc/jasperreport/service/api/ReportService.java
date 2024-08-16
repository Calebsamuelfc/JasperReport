package com.itssc.jasperreport.service.api;

import com.itssc.jasperreport.dto.request.ContractRequestDTO;
import com.itssc.jasperreport.dto.response.ServiceResponse;

public interface ReportService {
    ServiceResponse downloadCSV(ContractRequestDTO ContractRequestDTO);
    ServiceResponse downloadExcel(ContractRequestDTO ContractRequestDTO);
}
