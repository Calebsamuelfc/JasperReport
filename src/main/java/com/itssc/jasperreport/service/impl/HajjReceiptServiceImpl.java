package com.itssc.jasperreport.service.impl;

import com.itssc.jasperreport.dto.request.HajjReceiptRequestDTO;
import com.itssc.jasperreport.dto.request.TransactionReceiptRequestDTO;
import com.itssc.jasperreport.dto.response.RibResponse;
import com.itssc.jasperreport.models.KVTableInfo;
import com.itssc.jasperreport.service.api.HajjReceiptService;
import com.itssc.jasperreport.utils.LocalDateFormatUtil;
import com.itssc.jasperreport.utils.ResourceUtil;
import com.itssc.jasperreport.utils.Translation;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class HajjReceiptServiceImpl implements HajjReceiptService {

    @Override
    public RibResponse downloadHajjReceipt(HajjReceiptRequestDTO HajjReceiptRequestDTO) {
        Map<String, Object> parameters = new HashMap<>();
        Locale locale;
        if(HajjReceiptRequestDTO.getLegalEntityId().equalsIgnoreCase("SL6940001") || HajjReceiptRequestDTO.getLegalEntityId().equalsIgnoreCase("GM2700001")){
            locale = Locale.ENGLISH;
        }else{
            locale = Locale.FRENCH;
        }
        parameters.put("imgHeader", ResourceUtil.getHeaderImagePath(HajjReceiptRequestDTO.getLegalEntityId()));
        parameters.put("imgBackground", ResourceUtil.getBackgroundImgPath(HajjReceiptRequestDTO.getLegalEntityId()));
        parameters.put("title", Translation.HAJJRECEIPTTITLE.getTranslation(locale));
        parameters.put("date", LocalDateFormatUtil.formatFullDate(HajjReceiptRequestDTO.getLegalEntityId(), LocalDate.now().toString()));

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(getHajjReceiptInfo(HajjReceiptRequestDTO));
        parameters.put("datasource", dataSource);

        InputStream stream = HajjReceiptServiceImpl.class.getClassLoader().getResourceAsStream(ResourceUtil.getTransactionReceiptTemplate(HajjReceiptRequestDTO.getLegalEntityId()));

        JasperReport jasperReport = null;
        try {
            jasperReport = JasperCompileManager.compileReport(stream);
            JasperPrint jasperPrint = new JasperPrint();
            jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, (JRDataSource)new JREmptyDataSource());
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);
            String base64String = Base64.getEncoder().encodeToString(pdfBytes);
            return RibResponse.builder()
                    .base64String(base64String)
                    .responseStatus("success")
                    .responseCode("200")
                    .build();
        } catch (JRException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static List<KVTableInfo> getHajjReceiptInfo(HajjReceiptRequestDTO HajjReceiptRequestDTO){
        Locale locale;
        if(HajjReceiptRequestDTO.getLegalEntityId().equalsIgnoreCase("SL6940001") || HajjReceiptRequestDTO.getLegalEntityId().equalsIgnoreCase("GM2700001")){
            locale = Locale.ENGLISH;
        }else{
            locale = Locale.FRENCH;
        }
        List<KVTableInfo> result = new ArrayList<>();

        if(!StringUtils.isBlank(HajjReceiptRequestDTO.getTransactionDate()))
            result.add(KVTableInfo.builder().key(Translation.TRANSACTIONDATE.getTranslation(locale)).value(HajjReceiptRequestDTO.getTransactionDate()).build());
        if(!StringUtils.isBlank(HajjReceiptRequestDTO.getSender()))
            result.add(KVTableInfo.builder().key(Translation.SENDER.getTranslation(locale)).value(HajjReceiptRequestDTO.getSender()).build());
        if(!StringUtils.isBlank(HajjReceiptRequestDTO.getAccount()))
            result.add(KVTableInfo.builder().key(Translation.ACCOUNT.getTranslation(locale)).value(HajjReceiptRequestDTO.getAccount()).build());
        if(!StringUtils.isBlank(HajjReceiptRequestDTO.getBank()))
            result.add(KVTableInfo.builder().key(Translation.BANK.getTranslation(locale)).value(HajjReceiptRequestDTO.getBank()).build());
        if(!StringUtils.isBlank(HajjReceiptRequestDTO.getBeneficiary())){
            result.add(KVTableInfo.builder().key(Translation.BENEFICIARY.getTranslation(locale)).value(HajjReceiptRequestDTO.getBeneficiary()).build());
        }
        if(!StringUtils.isBlank(HajjReceiptRequestDTO.getDescription()))
            result.add(KVTableInfo.builder().key(Translation.DESCRIPTION.getTranslation(locale)).value(HajjReceiptRequestDTO.getDescription()).build());
        if(!StringUtils.isBlank(HajjReceiptRequestDTO.getTransactionAmount()))
            result.add(KVTableInfo.builder().key(Translation.TRANSACTIONAMOUNT.getTranslation(locale)).value(HajjReceiptRequestDTO.getTransactionAmount()).build());
        if(!StringUtils.isBlank(HajjReceiptRequestDTO.getTransactionReference()))
            result.add(KVTableInfo.builder().key(Translation.TRANSACTIONREFERENCE.getTranslation(locale)).value(HajjReceiptRequestDTO.getTransactionReference()).build());
        if(!StringUtils.isBlank(HajjReceiptRequestDTO.getTransactionAmount()))
            result.add(KVTableInfo.builder().key(Translation.PILGRIMNAME.getTranslation(locale)).value(HajjReceiptRequestDTO.getTransactionAmount()).build());
        if(!StringUtils.isBlank(HajjReceiptRequestDTO.getTransactionReference()))
            result.add(KVTableInfo.builder().key(Translation.OUSTANDINGBALANCE.getTranslation(locale)).value(HajjReceiptRequestDTO.getTransactionReference()).build());


        return result;
    }
}
