package com.mednet.externalpacs.hl7;

import java.util.ArrayList;
import java.util.List;

/**
 * Splits an ORU^R01 payload into {@link Hl7Segment}s. Vendor receivers map segments to the report DTO.
 */
public abstract class AbstractOruR01Parser implements Hl7Parser {

    @Override
    public List<Hl7Segment> parse(String rawHl7) throws Hl7ParseException {
        if (rawHl7 == null || rawHl7.isBlank()) {
            throw new Hl7ParseException("HL7 message is empty");
        }
        List<String> segmentLines = Hl7MessageNormalizer.splitSegments(rawHl7);
        if (segmentLines.isEmpty() || !segmentLines.get(0).startsWith("MSH")) {
            throw new Hl7ParseException("Message does not start with MSH");
        }

        Hl7Encoding encoding = Hl7Encoding.fromMsh(segmentLines.get(0));
        List<Hl7Segment> segments = new ArrayList<>(segmentLines.size());
        for (String line : segmentLines) {
            segments.add(new Hl7Segment(line, encoding));
        }
        return segments;
    }
}
