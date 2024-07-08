package com.itssc.jasperreport.service.impl;

import com.itssc.jasperreport.dto.request.RequestHeaders;
import com.itssc.jasperreport.dto.request.RibRequestDTO;
import com.itssc.jasperreport.dto.response.RibResponse;
import com.itssc.jasperreport.models.KVTableInfo;
import com.itssc.jasperreport.service.api.RibService;
import com.itssc.jasperreport.utils.ResourceUtil;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RibServiceImpl implements RibService {
    private static final Logger logger = LogManager.getLogger(RibServiceImpl.class);
    @Override
    public RibResponse  downloadRib(RequestHeaders headers, RibRequestDTO ribRequest) {
        Map<String,Object> parameters = new HashMap<>();
        parameters.put("fullName", ribRequest.getFullname());
        parameters.put("currency", ribRequest.getCurrency());

        String domiciliation = ribRequest.getDomiciliation();
        if(StringUtils.isBlank(ribRequest.getDomiciliation())){
            if (ribRequest.getLegalEntityId().equalsIgnoreCase("BF2260001")){
                domiciliation = "BURKINA FASO";
            } else if(ribRequest.getLegalEntityId().equalsIgnoreCase("GN3240001")){
                domiciliation = "VISTA BANK";
            }else{
                domiciliation = "REPUBLIQUE";
            }
        }
        List<KVTableInfo> Riblist = getRibTableInfo(ribRequest);
        InputStream path = RibServiceImpl.class.getClassLoader().getResourceAsStream(ResourceUtil.getRibTemplate());
        try {
            JasperReport jasperReport = JasperCompileManager.compileReport(path);
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(Riblist);
            parameters.put("tableData",dataSource);
            parameters.put("imgLogo",ResourceUtil.getImagePath(ribRequest.getLegalEntityId()));
            parameters.put("domiciliation",domiciliation);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,new JREmptyDataSource());
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);
            String base64String = Base64.getEncoder().encodeToString(pdfBytes);

            return RibResponse.builder()
                    .base64String(base64String)
                    .responseStatus("success")
                    .responseCode("200")
                    .build();
        } catch (JRException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public  List<KVTableInfo> getRibTableInfo(RibRequestDTO ribData){
        List<KVTableInfo> result = new ArrayList<>();
        logger.error("rib:" + ribData.getRib());
        logger.error("rib length:" + ribData.getRib().length());
        if(ribData.getRib() != null & !ribData.getRib().equals("") & !StringUtils.isBlank(ribData.getRib())){
            result.add(KVTableInfo.builder().key("RIB:").value(ribData.getRib()).build());
        }
        logger.error("iban:" + ribData.getIban());
        logger.error("iban length:" + ribData.getIban().length());
        if(ribData.getIban() != null & !ribData.getIban().equals("") & !StringUtils.isBlank(ribData.getIban())){
            result.add(KVTableInfo.builder().key("IBAN:").value(ribData.getIban()).build());
        }
        switch (ribData.getLegalEntityId()) {
            case "BF2260001" -> result.add(KVTableInfo.builder().key("BIC:").value("BICIBFBXXXX").build());
            case "GN3240001" -> result.add(KVTableInfo.builder().key("BIC:").value("FIBLGNGNXXX").build());
            case "GN2240001" -> result.add(KVTableInfo.builder().key("BIC:").value("BICIGNCXXXX").build());
            default -> {
            }
        }

        return  result;
    }
}
