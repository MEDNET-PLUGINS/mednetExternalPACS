package com.mednet.externalpacs.reports.dto.sendToMednet;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class ReportDetails {

    private String testName;
    private String testCode;
    private String testLoinc;

    //in case of L1
    private String value;
    private String unit;
    private String referenceRange;
    /** OBX-8 normal/abnormal flag when L1 single result. */
    private String abnormalFlag;
    private String isCritical;
    private String minValue;
    private String maxValue;

    //in case of L2
    private List<ReportDetailsParameter> parameters;
    private String freeText;

    public void addParam(ReportDetailsParameter parameter) {
        if (parameters == null) {
            parameters = new ArrayList<>();
        }
        parameters.add(parameter);
    }

    @Override
    public String toString() {
        return "{"
                + "testName='"
                + testName
                + '\''
                + ", testCode='"
                + testCode
                + '\''
                + ", testLoinc='"
                + testLoinc
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
                + ", parameters="
                + parameters
                + ", freeText='"
                + freeText
                + '\''
                + '}';
    }
}
