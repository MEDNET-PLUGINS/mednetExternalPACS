package com.mednet.externalpacs.hl7;

import java.util.List;

public interface Hl7Parser {

    /**
     * HL7 version this parser is built for, e.g. "2.3" or "2.4".
     */
    String supportedVersion();

    boolean supports(String versionId);

    List<Hl7Segment> parse(String rawHl7) throws Hl7ParseException;
}
