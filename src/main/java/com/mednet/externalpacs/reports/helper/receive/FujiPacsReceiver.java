package com.mednet.externalpacs.reports.helper.receive;

import com.mednet.externalpacs.hl7.Hl7ParserFactory;
import com.mednet.externalpacs.hl7.Hl7Segment;
import com.mednet.externalpacs.hl7.ObxObservationValueUtil;
import com.mednet.externalpacs.reports.dto.receive.PACSDiagnosticsReport;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FujiPacsReceiver extends AbstractPacsReportReceiver implements PacsReportReceiver {

    @Override
    public PACSDiagnosticsReport processRawHl7(String rawHl7, String pacsName) throws Exception {
        System.out.println("FujiPacsReceiver :: " + pacsName);
        return parseHl7(rawHl7);
    }

    private PACSDiagnosticsReport parseHl7(String rawHl7) throws Exception {
        if (StringUtils.isBlank(rawHl7)) {
            throw new Exception("raw HL7 is blank");
        }
        List<Hl7Segment> segments = Hl7ParserFactory.forMessage(rawHl7).parse(rawHl7.trim());
        PACSDiagnosticsReport report = new PACSDiagnosticsReport();
        for (Hl7Segment segment : segments) {
            switch (segment.getId()) {
                case "PID":
                    mapPid(segment, report);
                    break;
                case "ORC":
                    mapOrc(segment, report);
                    break;
                case "OBR":
                    mapObr(segment, report);
                    break;
                case "OBX":
                    mapObx(segment, report);
                    break;
                case "NTE":
                    mapNte(segment, report);
                    break;
                default:
                    break;
            }
        }
        validate(report);
        return report;
    }

    private void mapPid(Hl7Segment pid, PACSDiagnosticsReport report) {
        if (StringUtils.isBlank(report.getMrn())) {
            report.setMrn(StringUtils.firstNonBlank(pid.component(3, 1), pid.field(2), pid.field(3)));
        }
    }

    private void mapOrc(Hl7Segment orc, PACSDiagnosticsReport report) {
        if (StringUtils.isBlank(report.getAccessionNo())) {
            report.setAccessionNo(StringUtils.firstNonBlank(orc.field(2), orc.field(3)));
        }
        if (StringUtils.isBlank(report.getTechnicianCode())) {
            report.setTechnicianCode(orc.component(10, 1));
        }
    }

    private void mapObr(Hl7Segment obr, PACSDiagnosticsReport report) {
        if (StringUtils.isBlank(report.getAccessionNo())) {
            report.setAccessionNo(StringUtils.firstNonBlank(obr.field(2), obr.field(3)));
        }
        if (StringUtils.isBlank(report.getMedicalLicenceNumberOrEmpCode())) {
            report.setMedicalLicenceNumberOrEmpCode(obr.personId(32));
        }
        if (StringUtils.isBlank(report.getDocName())) {
            report.setDocName(obr.personDisplayName(32));
        }
        String technician = StringUtils.firstNonBlank(obr.subcomponent(34, 1), obr.component(34, 1));
        if (StringUtils.isNotBlank(technician)) {
            report.setTechnicianCode(technician);
        }
    }

    private void mapObx(Hl7Segment obx, PACSDiagnosticsReport report) {
        String rawValue = StringUtils.firstNonBlank(obx.field(5), obx.field(6));
        String value = ObxObservationValueUtil.normalize(rawValue, obx.getEncoding());
        if (StringUtils.isNotBlank(value)) {
            report.setObxObservationValue(ObxObservationValueUtil.append(report.getObxObservationValue(), value));
        }
        String observerId = obx.personId(16);
        if (StringUtils.isNotBlank(observerId)) {
            report.setMedicalLicenceNumberOrEmpCode(observerId);
        }
        String observerName = obx.personDisplayName(16);
        if (StringUtils.isNotBlank(observerName)) {
            report.setDocName(observerName);
        }
    }

    private void mapNte(Hl7Segment nte, PACSDiagnosticsReport report) {
        String comment = nte.getEncoding().unescape(StringUtils.firstNonBlank(nte.field(3), nte.field(2)));
        if (StringUtils.isBlank(comment)) {
            return;
        }
        String existing = report.getObxObservationValue();
        report.setObxObservationValue(StringUtils.isBlank(existing) ? comment : existing + "\n" + comment);
    }

    private void validate(PACSDiagnosticsReport report) throws Exception {
        if (StringUtils.isBlank(report.getMrn())) {
            throw new Exception("PID-3 : Patient Identifier missing");
        }
        if (StringUtils.isBlank(report.getAccessionNo())) {
            throw new Exception("ORC-2 / OBR-2 : Accession No missing");
        }
        if (StringUtils.isBlank(report.getObxObservationValue())) {
            throw new Exception("OBX-5 : Observation Result missing");
        }
    }
}
