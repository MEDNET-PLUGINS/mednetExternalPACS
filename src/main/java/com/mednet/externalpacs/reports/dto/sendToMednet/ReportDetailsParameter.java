package com.mednet.externalpacs.reports.dto.sendToMednet;

import lombok.Data;

@Data
public class ReportDetailsParameter {
    private String testName;
    private String paramName;
    private String paramCode;
    private String paramLoinc;
    private String value;
    private String unit;
    private String referenceRange;
    private String minValue;
    private String maxValue;
    /** OBX-8 normal/abnormal flag (e.g. N, A, H, L). */
    private String abnormalFlag;
    private String isCritical;

    @Override
    public String toString() {
        return "{"
                + "testName='"
                + testName
                + '\''
                + ", paramName='"
                + paramName
                + '\''
                + ", paramCode='"
                + paramCode
                + '\''
                + ", paramLoinc='"
                + paramLoinc
                + '\''
                + ", value='"
                + value
                + '\''
                + ", unit='"
                + unit
                + '\''
                + ", referenceRange='"
                + referenceRange
                + '\''
                + ", abnormalFlag='"
                + abnormalFlag
                + '\''
                + ", isCritical='"
                + isCritical
                + '\''
                + ", minValue='"
                + minValue
                + '\''
                + ", maxValue='"
                + maxValue
                + '\''
                + '}';
    }
}
