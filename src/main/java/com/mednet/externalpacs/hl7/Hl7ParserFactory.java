package com.mednet.externalpacs.hl7;

import com.mednet.externalpacs.hl7.v23.OruR01V23Parser;
import com.mednet.externalpacs.hl7.v24.OruR01V24Parser;
import com.mednet.externalpacs.hl7.v27.OruR01V27Parser;

/**
 * Picks the ORU parser from MSH-12. Defaults to 2.7 (Fenix / current PACS) when unmatched.
 */
public final class Hl7ParserFactory {

    private static final Hl7Parser V23 = new OruR01V23Parser();
    private static final Hl7Parser V24 = new OruR01V24Parser();
    private static final Hl7Parser V27 = new OruR01V27Parser();

    private Hl7ParserFactory() {
    }

    public static Hl7Parser forMessage(String rawHl7) {
        return forVersion(Hl7MessageNormalizer.extractVersion(rawHl7));
    }

    public static Hl7Parser forVersion(String versionId) {
        if (V23.supports(versionId)) {
            return V23;
        }
        if (V24.supports(versionId)) {
            return V24;
        }
        return V27;
    }

    public static Hl7Parser v23() {
        return V23;
    }

    public static Hl7Parser v24() {
        return V24;
    }

    public static Hl7Parser v27() {
        return V27;
    }
}
