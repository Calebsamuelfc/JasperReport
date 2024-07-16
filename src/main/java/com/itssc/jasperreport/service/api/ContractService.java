package com.itssc.jasperreport.service.api;

import com.itssc.jasperreport.dto.request.ContractRequestDTO;
import com.itssc.jasperreport.dto.response.ServiceResponse;

public interface ContractService {
    ServiceResponse DownloadContract(ContractRequestDTO ContractRequestDTO);
    ServiceResponse downloadContractCsv(ContractRequestDTO ContractRequestDTO);
    ServiceResponse downloadContractExcel(ContractRequestDTO ContractRequestDTO);
}
