package com.mednet.externalpacs.reports.service.impl;

import com.mednet.externalpacs.common.exception.BusinessValidationException;
import com.mednet.externalpacs.reports.helper.receive.CentricityGePacsReceiver;
import com.mednet.externalpacs.reports.helper.receive.DeeptekPacsReceiver;
import com.mednet.externalpacs.reports.helper.receive.FujiPacsReceiver;
import com.mednet.externalpacs.reports.helper.receive.ImageBytesPacsReceiver;
import com.mednet.externalpacs.reports.helper.receive.MedSynapticsPacsReceiver;
import com.mednet.externalpacs.reports.helper.receive.PuruPacsReceiver;
import com.mednet.externalpacs.reports.helper.receive.RadspaPacsReceiver;
import com.mednet.externalpacs.reports.service.PacsReports;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PacsReportsImpl implements PacsReports {

    @Autowired
    private CentricityGePacsReceiver centricityGePacsReceiver;

    @Autowired
    private MedSynapticsPacsReceiver medSynapticsPacsReceiver;

    @Autowired
    private ImageBytesPacsReceiver imageBytesPacsReceiver;

    @Autowired
    private DeeptekPacsReceiver deeptekPacsReceiver;

    @Autowired
    private FujiPacsReceiver fujiPacsReceiver;

    @Autowired
    private RadspaPacsReceiver radspaPacsReceiver;

    @Autowired
    private PuruPacsReceiver puruPacsReceiver;

    @Override
    public String receivePacsReport(String rawHl7, String pacsName) throws Exception {
        String pacs = StringUtils.toRootUpperCase(pacsName);
        if (pacs != null) {
            pacs = pacs.replace('_', ' ').replace('-', ' ').replaceAll("\\s+", " ").trim();
        }
        if (StringUtils.isBlank(pacs)) {
            throw new BusinessValidationException("Unknown pacs: " + pacsName);
        }

        switch (pacs) {
            case "CENTRICITY_GE":
                centricityGePacsReceiver.processReportData(rawHl7, pacsName);
                break;
            case "MEDSYNAPTICS":
                medSynapticsPacsReceiver.processReportData(rawHl7, pacsName);
                break;
            case "IMAGEBYTES":
                imageBytesPacsReceiver.processReportData(rawHl7, pacsName);
                break;
            case "DEEPTEK":
                deeptekPacsReceiver.processReportData(rawHl7, pacsName);
                break;
            case "FUJI":
                fujiPacsReceiver.processReportData(rawHl7, pacsName);
                break;
            case "RADSPA":
                radspaPacsReceiver.processReportData(rawHl7, pacsName);
                break;
            case "PURUPACS":
                puruPacsReceiver.processReportData(rawHl7, pacsName);
                break;
            default:
                throw new BusinessValidationException("Unknown pacs: " + pacsName);
        }
        return "success";
    }

}
