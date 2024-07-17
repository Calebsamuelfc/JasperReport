package com.itssc.jasperreport.service.api;

import com.itssc.jasperreport.dto.request.HajjReceiptRequestDTO;
import com.itssc.jasperreport.dto.response.MobileServiceResponse;
import com.itssc.jasperreport.dto.response.ServiceResponse;

public interface HajjReceiptService {
    ServiceResponse downloadHajjReceipt(HajjReceiptRequestDTO HajjReceiptRequestDTO);

    MobileServiceResponse downloadHajjReceiptMobile (HajjReceiptRequestDTO HajjReceiptRequestDTO);
}
