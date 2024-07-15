package com.itssc.jasperreport.service.impl;

import com.itssc.jasperreport.dto.request.TransactionReceiptRequestDTO;
import com.itssc.jasperreport.dto.response.MobileServiceResponse;
import com.itssc.jasperreport.dto.response.ServiceResponse;
import com.itssc.jasperreport.models.KVTableInfo;
import com.itssc.jasperreport.service.api.TransactionReceiptService;
import com.itssc.jasperreport.utils.FileUtil;
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
    public ServiceResponse downloadSingleTransactionReceipt(TransactionReceiptRequestDTO transactionReceiptRequestDTO) {
        Locale locale = determineLocale(transactionReceiptRequestDTO.getLegalEntityId());

        Map<String, Object> parameters = prepareParameters(transactionReceiptRequestDTO, locale);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(
                getTransactionInfo(transactionReceiptRequestDTO, locale));
        parameters.put("datasource", dataSource);

        try (InputStream stream = getResourceStream(transactionReceiptRequestDTO.getLegalEntityId())) {
            JasperReport jasperReport = JasperCompileManager.compileReport(stream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);
            String base64String = Base64.getEncoder().encodeToString(pdfBytes);
            return ServiceResponse.builder()
                    .base64String(base64String)
                    .responseStatus("success")
                    .responseCode("200")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public MobileServiceResponse downloadSingleTransactionReceiptMobile(TransactionReceiptRequestDTO transactionReceiptRequestDTO) {
        Locale locale = determineLocale(transactionReceiptRequestDTO.getLegalEntityId());

        Map<String, Object> parameters = prepareParameters(transactionReceiptRequestDTO, locale);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(
                getTransactionInfo(transactionReceiptRequestDTO, locale));
        parameters.put("datasource", dataSource);

        try (InputStream stream = getResourceStream(transactionReceiptRequestDTO.getLegalEntityId())) {
            JasperReport jasperReport = JasperCompileManager.compileReport(stream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);
            String fileName = FileUtil.saveFile(pdfBytes);
            return MobileServiceResponse.builder()
                    .base64String(fileName)
                    .responseStatus("success")
                    .responseCode("200")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Locale determineLocale(String legalEntityId) {
        return legalEntityId.equalsIgnoreCase("SL6940001") || legalEntityId.equalsIgnoreCase("GM2700001") ?
                Locale.ENGLISH : Locale.FRENCH;
    }

    private Map<String, Object> prepareParameters(TransactionReceiptRequestDTO transactionReceiptRequestDTO, Locale locale) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("imgHeader", ResourceUtil.getHeaderImagePath(transactionReceiptRequestDTO.getLegalEntityId()));
        parameters.put("imgBackground", ResourceUtil.getBackgroundImgPath(transactionReceiptRequestDTO.getLegalEntityId()));
        parameters.put("title", Translation.TRANSACTIONRECEIPTTITLE.getTranslation(locale));
        parameters.put("date", LocalDateFormatUtil.formatFullDate(transactionReceiptRequestDTO.getLegalEntityId(), LocalDate.now().toString()));
        return parameters;
    }

    private List<KVTableInfo> getTransactionInfo(TransactionReceiptRequestDTO transactionReceiptRequestDTO, Locale locale) {
        List<KVTableInfo> result = new ArrayList<>();
        if (!StringUtils.isBlank(transactionReceiptRequestDTO.getTransactionDate()))
            result.add(new KVTableInfo(Translation.TRANSACTIONDATE.getTranslation(locale), transactionReceiptRequestDTO.getTransactionDate()));
        if (!StringUtils.isBlank(transactionReceiptRequestDTO.getSender()))
            result.add(new KVTableInfo(Translation.SENDER.getTranslation(locale), transactionReceiptRequestDTO.getSender()));
        if (!StringUtils.isBlank(transactionReceiptRequestDTO.getAccount()))
            result.add(new KVTableInfo(Translation.ACCOUNT.getTranslation(locale), transactionReceiptRequestDTO.getAccount()));
        if (!StringUtils.isBlank(transactionReceiptRequestDTO.getBank()))
            result.add(new KVTableInfo(Translation.BANK.getTranslation(locale), transactionReceiptRequestDTO.getBank()));
        if (!StringUtils.isBlank(transactionReceiptRequestDTO.getBeneficiary()))
            result.add(new KVTableInfo(Translation.BENEFICIARY.getTranslation(locale), transactionReceiptRequestDTO.getBeneficiary()));
        if (!StringUtils.isBlank(transactionReceiptRequestDTO.getDescription()))
            result.add(new KVTableInfo(Translation.DESCRIPTION.getTranslation(locale), transactionReceiptRequestDTO.getDescription()));
        if (!StringUtils.isBlank(transactionReceiptRequestDTO.getTransactionAmount()))
            result.add(new KVTableInfo(Translation.TRANSACTIONAMOUNT.getTranslation(locale), transactionReceiptRequestDTO.getTransactionAmount()));
        if (!StringUtils.isBlank(transactionReceiptRequestDTO.getTransactionReference()))
            result.add(new KVTableInfo(Translation.TRANSACTIONREFERENCE.getTranslation(locale), transactionReceiptRequestDTO.getTransactionReference()));
        return result;
    }

    private InputStream getResourceStream(String legalEntityId) {
        return TransactionReceiptServiceImpl.class.getClassLoader()
                .getResourceAsStream(ResourceUtil.getTransactionReceiptTemplate(legalEntityId));
    }
}
