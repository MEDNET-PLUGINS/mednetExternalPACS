package com.mednet.externalpacs.hl7.v24;

import com.mednet.externalpacs.hl7.AbstractOruR01Parser;

/**
 * HL7 v2.4 ORU^R01 parser. Splits the message into {@link com.mednet.externalpacs.hl7.Hl7Segment}s.
 */
public class OruR01V24Parser extends AbstractOruR01Parser {

    @Override
    public String supportedVersion() {
        return "2.4";
    }

    @Override
    public boolean supports(String versionId) {
        return versionId != null && versionId.trim().startsWith("2.4");
    }
}
