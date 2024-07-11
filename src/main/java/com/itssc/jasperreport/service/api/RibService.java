package com.itssc.jasperreport.service.api;

import com.itssc.jasperreport.dto.request.RequestHeaders;
import com.itssc.jasperreport.dto.request.RibRequestDTO;
import com.itssc.jasperreport.dto.response.ServiceResponse;

public interface RibService {
    ServiceResponse downloadRib(RequestHeaders headers, RibRequestDTO ribRequest);
}
