package com.itssc.jasperreport.service.impl;

import com.itssc.jasperreport.dto.request.TransactionReceiptRequestDTO;
import com.itssc.jasperreport.dto.response.RibResponse;
import com.itssc.jasperreport.models.KVTableInfo;
import com.itssc.jasperreport.service.api.TransactionReceiptService;
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
public class TransactionReceiptServiceImpl implements TransactionReceiptService {

    @Override
    public RibResponse downloadSingleTransactionReceipt(TransactionReceiptRequestDTO transactionReceiptRequestDTO) {
        Map<String, Object> parameters = new HashMap<>();
        Locale locale = Locale.ENGLISH;
        if(transactionReceiptRequestDTO.getLegalEntityId().equalsIgnoreCase("SL6940001") || transactionReceiptRequestDTO.getLegalEntityId().equalsIgnoreCase("GM2700001")){
            locale = Locale.ENGLISH;
        }else{
            locale = Locale.FRENCH;
        }
        parameters.put("imgHeader", ResourceUtil.getHeaderImagePath(transactionReceiptRequestDTO.getLegalEntityId()));
        parameters.put("imgBackground", ResourceUtil.getBackgroundImgPath(transactionReceiptRequestDTO.getLegalEntityId()));
        parameters.put("title", Translation.TRANSACTIONRECEIPTTITLE.getTranslation(locale));
        parameters.put("date", LocalDateFormatUtil.formatFullDate(transactionReceiptRequestDTO.getLegalEntityId(), LocalDate.now().toString()));


        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(getTransactionInfoEnglish(transactionReceiptRequestDTO));
        parameters.put("datasource", dataSource);

        InputStream stream = TransactionReceiptServiceImpl.class.getClassLoader().getResourceAsStream(ResourceUtil.getTransactionReceiptTemplate(transactionReceiptRequestDTO.getLegalEntityId()));

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

    public static List<KVTableInfo> getTransactionInfoEnglish(TransactionReceiptRequestDTO transactionReceiptRequestDTO){
        Locale locale = Locale.ENGLISH;
        if(transactionReceiptRequestDTO.getLegalEntityId().equalsIgnoreCase("SL6940001") || transactionReceiptRequestDTO.getLegalEntityId().equalsIgnoreCase("GM2700001")){
            locale = Locale.ENGLISH;
        }else{
            locale = Locale.FRENCH;
        }
        List<KVTableInfo> result = new ArrayList<>();

        if(!StringUtils.isBlank(transactionReceiptRequestDTO.getTransactionDate()))
            result.add(KVTableInfo.builder().key(Translation.TRANSACTIONDATE.getTranslation(locale)).value(transactionReceiptRequestDTO.getTransactionDate()).build());
        if(!StringUtils.isBlank(transactionReceiptRequestDTO.getSender()))
            result.add(KVTableInfo.builder().key(Translation.SENDER.getTranslation(locale)).value(transactionReceiptRequestDTO.getSender()).build());
        if(!StringUtils.isBlank(transactionReceiptRequestDTO.getAccount()))
            result.add(KVTableInfo.builder().key(Translation.ACCOUNT.getTranslation(locale)).value(transactionReceiptRequestDTO.getAccount()).build());
        if(!StringUtils.isBlank(transactionReceiptRequestDTO.getBank()))
            result.add(KVTableInfo.builder().key(Translation.BANK.getTranslation(locale)).value(transactionReceiptRequestDTO.getBank()).build());
        if(!StringUtils.isBlank(transactionReceiptRequestDTO.getBeneficiary())){
            result.add(KVTableInfo.builder().key(Translation.BENEFICIARY.getTranslation(locale)).value(transactionReceiptRequestDTO.getBeneficiary()).build());
        }
        if(!StringUtils.isBlank(transactionReceiptRequestDTO.getDescription()))
            result.add(KVTableInfo.builder().key(Translation.DESCRIPTION.getTranslation(locale)).value(transactionReceiptRequestDTO.getDescription()).build());
        if(!StringUtils.isBlank(transactionReceiptRequestDTO.getTransactionAmount()))
            result.add(KVTableInfo.builder().key(Translation.TRANSACTIONAMOUNT.getTranslation(locale)).value(transactionReceiptRequestDTO.getTransactionAmount()).build());
        if(!StringUtils.isBlank(transactionReceiptRequestDTO.getTransactionReference()))
            result.add(KVTableInfo.builder().key(Translation.TRANSACTIONREFERENCE.getTranslation(locale)).value(transactionReceiptRequestDTO.getTransactionReference()).build());

        return result;
    }
}
