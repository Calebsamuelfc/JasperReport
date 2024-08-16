package com.itssc.jasperreport.service.api;



import com.itssc.jasperreport.dto.request.ContractRequestDTO;
import com.itssc.jasperreport.dto.response.ServiceResponse;

public interface SvipService {
    ServiceResponse downloadSvipCsv(ContractRequestDTO contractRequestDTO);
    ServiceResponse downloadSvipExcel(ContractRequestDTO contractRequestDTO);
}
