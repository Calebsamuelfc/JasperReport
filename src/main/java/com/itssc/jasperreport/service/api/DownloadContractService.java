package com.itssc.jasperreport.service.api;

import com.itssc.jasperreport.dto.request.downloadContractRequestDTO;
import com.itssc.jasperreport.dto.response.ServiceResponse;

public interface DownloadContractService {
    ServiceResponse DownloadContract(downloadContractRequestDTO downloadContractRequestDTO);
}
