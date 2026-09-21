package com.mednet.externalpacs.hl7.v27;

import com.mednet.externalpacs.hl7.v24.OruR01V24Parser;

/**
 * HL7 v2.5 / v2.6 / v2.7 ORU^R01 parser. FenixPACS sends 2.7 with HTML in OBX-5.
 * Field layout matches 2.4 for the segments used here.
 */
public class OruR01V27Parser extends OruR01V24Parser {

    @Override
    public String supportedVersion() {
        return "2.7";
    }

    @Override
    public boolean supports(String versionId) {
        if (versionId == null) {
            return false;
        }
        String version = versionId.trim();
        return version.startsWith("2.5") || version.startsWith("2.6") || version.startsWith("2.7");
    }
}
