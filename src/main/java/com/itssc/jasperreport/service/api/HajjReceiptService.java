package com.itssc.jasperreport.service.api;

import com.itssc.jasperreport.dto.request.HajjReceiptRequestDTO;
import com.itssc.jasperreport.dto.response.RibResponse;

public interface HajjReceiptService {
    RibResponse downloadHajjReceipt(HajjReceiptRequestDTO HajjReceiptRequestDTO);
}
