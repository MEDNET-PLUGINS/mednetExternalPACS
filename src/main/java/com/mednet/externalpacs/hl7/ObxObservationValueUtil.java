package com.mednet.externalpacs.hl7;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Normalizes OBX observation text after fields have been split.
 * Not used while parsing HL7 delimiters / segments.
 */
public final class ObxObservationValueUtil {

    private static final Pattern HEX_ESCAPE = Pattern.compile("\\\\X([0-9A-Fa-f]{2})\\\\");

    private ObxObservationValueUtil() {
    }

    public static String normalize(String rawObservationValue) {
        return normalize(rawObservationValue, Hl7Encoding.hl7Default());
    }

    public static String normalize(String rawObservationValue, Hl7Encoding encoding) {
        if (rawObservationValue == null) {
            return null;
        }
        if (rawObservationValue.isEmpty()) {
            return rawObservationValue;
        }
        return unescapeHl7(repairUtf8Mojibake(rawObservationValue));
    }

    public static String append(String existing, String nextNormalized) {
        if (nextNormalized == null || nextNormalized.isBlank()) {
            return existing;
        }
        if (existing == null || existing.isBlank()) {
            return nextNormalized;
        }
        return existing + "\n" + nextNormalized;
    }

    private static String unescapeHl7(String value) {
        if (value == null) {
            return null;
        }

        String result = value;

        // Carriage return / line feed encoded as hex escapes
        result = result.replace("\\X0D\\", "\r");
        result = result.replace("\\X0A\\", "\n");

        // Formatted-text carriage return escape sequence (used in TX/FT fields)
        result = result.replace("\\.br\\", "\n");

        // Collapse the common \r\n pair (from \X0D\\X0A\ back to back) into a single newline
        result = result.replace("\r\n", "\n");
        result = result.replace("\r", "\n");

        // Standard HL7 escape chars
        result = result.replace("\\F\\", "|");   // field separator
        result = result.replace("\\S\\", "^");   // component separator
        result = result.replace("\\T\\", "&");   // subcomponent separator
        result = result.replace("\\R\\", "~");   // repetition separator
        result = result.replace("\\E\\", "\\");  // escape char itself (do this last)

        result = result.replace("\n", "<br>");
        return result;
    }

    private static String decodeGenericHexEscapes(String value) {
        Matcher m = HEX_ESCAPE.matcher(value);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            int code = Integer.parseInt(m.group(1), 16);
            m.appendReplacement(sb, Matcher.quoteReplacement(String.valueOf((char) code)));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * Only repair when UTF-8 was decoded as Latin-1 ({@code Â«}). Applying this to valid
     * Unicode ({@code «}) would encode as 0xAB and re-decode as UTF-8 replacement chars.
     */
    private static String repairUtf8Mojibake(String value) {
        if (value == null || value.isEmpty() || !looksLikeUtf8Mojibake(value)) {
            return value;
        }
        return new String(value.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }

    private static boolean looksLikeUtf8Mojibake(String value) {
        return value.contains("\u00C2\u00AB")
                || value.contains("\u00C2\u00BB")
                || value.contains("\u00E2\u0080");
    }
}
