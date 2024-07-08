package com.itssc.jasperreport.service.api;

import com.itssc.jasperreport.dto.request.RequestHeaders;
import com.itssc.jasperreport.dto.request.RibRequestDTO;
import com.itssc.jasperreport.dto.response.RibResponse;

import java.util.Map;

public interface RibService {
    RibResponse downloadRib(RequestHeaders headers, RibRequestDTO ribRequest);
}
