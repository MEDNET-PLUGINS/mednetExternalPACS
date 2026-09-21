package com.mednet.externalpacs.hl7.v23;

import com.mednet.externalpacs.hl7.AbstractOruR01Parser;

/**
 * HL7 v2.3 ORU^R01 parser for PACS radiology reports.
 */
public class OruR01V23Parser extends AbstractOruR01Parser {

    @Override
    public String supportedVersion() {
        return "2.3";
    }

    @Override
    public boolean supports(String versionId) {
        if (versionId == null) {
            return false;
        }
        String version = versionId.trim();
        return version.equals("2.3") || version.startsWith("2.3.");
    }
}
