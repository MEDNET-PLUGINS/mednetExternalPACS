package com.mednet.externalpacs.reports.dto.sendToMednet;

import java.util.List;
import lombok.Data;

@Data
public class ReportHeader {

    private String mrn;

    private String orderNo;
    private String externalOrderNo;
    private String sampleID;
    private String deptName;
    private String type;                        // Numeric or Freetext

    private String reportName;
    private String reportDate;
    private String isCriticalReport;

    private List<ReportDetails> reportDetails;

    /*** report in byte array ***/
    private byte[] attachment;

    /*** report in base64 ***/
    private String reportBase64;

    /*** report file name ***/
    private String filename;

    /*** report file path at specific location if needed outside HIS defined reports folder ***/
    private String filepath;


    private String source;                      // MIGRATION / EXTERNAL_LAB / PACS_ORU

    private List<ReportDoctor> doctors;

    private String technicianCode;

    @Override
    public String toString() {
        return "{"
                + "mrn='"
                + mrn
                + '\''
                + ", orderNo='"
                + orderNo
                + '\''
                + ", externalOrderNo='"
                + externalOrderNo
                + '\''
                + ", sampleID='"
                + sampleID
                + '\''
                + ", deptName='"
                + deptName
                + '\''
                + ", type='"
                + type
                + '\''
                + ", reportName='"
                + reportName
                + '\''
                + ", reportDate='"
                + reportDate
                + '\''
                + ", reportDetails="
                + reportDetails
                + ", attachment="
                + attachmentSummary(attachment)
                + ", reportBase64="
                + base64Summary(reportBase64)
                + ", filename='"
                + filename
                + '\''
                + ", filepath='"
                + filepath
                + '\''
                + ", source='"
                + source
                + '\''
                + ", doctors="
                + doctors
                + ", technicianCode='"
                + technicianCode
                + '\''
                + '}';
    }

    private static String attachmentSummary(byte[] attachment) {
        if (attachment == null) {
            return "null";
        }
        return attachment.length + " bytes";
    }

    private static String base64Summary(String base64) {
        if (base64 == null) {
            return "null";
        }
        return base64.length() + " chars";
    }
}
