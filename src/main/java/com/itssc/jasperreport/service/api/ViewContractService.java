package com.itssc.jasperreport.service.api;

import com.itssc.jasperreport.dto.request.ContractRequestDTO;
import com.itssc.jasperreport.dto.response.ServiceResponse;

public interface ViewContractService {
    ServiceResponse viewContractPdf(ContractRequestDTO ContractRequestDTO);
    ServiceResponse viewContractCsv(ContractRequestDTO ContractRequestDTO);
    ServiceResponse viewContractExcel(ContractRequestDTO ContractRequestDTO);
}
