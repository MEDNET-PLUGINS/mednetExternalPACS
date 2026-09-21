package com.mednet.externalpacs.reports;

import com.mednet.externalpacs.hl7.Hl7MessageNormalizer;
import com.mednet.externalpacs.reports.dto.receive.PACSDiagnosticsReport;
import com.mednet.externalpacs.reports.helper.receive.AbstractPacsReportReceiver;
import com.mednet.externalpacs.reports.helper.receive.CentricityGePacsReceiver;
import com.mednet.externalpacs.reports.helper.receive.DeeptekPacsReceiver;
import com.mednet.externalpacs.reports.helper.receive.FujiPacsReceiver;
import com.mednet.externalpacs.reports.helper.receive.ImageBytesPacsReceiver;
import com.mednet.externalpacs.reports.helper.receive.MedSynapticsPacsReceiver;
import com.mednet.externalpacs.reports.helper.receive.PuruPacsReceiver;
import com.mednet.externalpacs.reports.helper.receive.RadspaPacsReceiver;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Single execution point for every facility ORU sample.
 * <p>
 * Each facility folder holds one HL7 dump named after its PACS ({@code centricityge.txt},
 * {@code medsynaptics.txt}, ...) and a dump may contain several MSH messages. Samples are read
 * from {@code /usr/local/mednet/mednetFile/ORU/{facility}/} and fall back to the test classpath.
 */
public class PacsOruReportTest {

    private static final Logger log = LoggerFactory.getLogger(PacsOruReportTest.class);

    private static final Path ORU_DIR = Path.of("/usr/local/mednet/mednetFile/ORU");

    static Stream<Facility> facilities() {
        return Stream.of(
                new Facility("nci", "Centricity - GE", CentricityGePacsReceiver::new, 1, 0),
                new Facility("balco", "Centricity - GE", CentricityGePacsReceiver::new, 2, 0),
                new Facility("sarvodaya", "MedSynaptics", MedSynapticsPacsReceiver::new, 3, 1),
                new Facility("noble", "MedSynaptics", MedSynapticsPacsReceiver::new, 1, 0),
                new Facility("mvr", "MedSynaptics", MedSynapticsPacsReceiver::new, 1, 0),
                new Facility("vector", "MedSynaptics", MedSynapticsPacsReceiver::new, 1, 0),
                new Facility("rubycarecooper", "ImageBytes", ImageBytesPacsReceiver::new, 1, 0),
                new Facility("madhuban", "ImageBytes", ImageBytesPacsReceiver::new, 1, 0),
                new Facility("rubycaretherogaon", "Deeptek", DeeptekPacsReceiver::new, 1, 0),
                new Facility("peerless", "Fuji", FujiPacsReceiver::new, 2, 0),
                new Facility("nairobi", "Radspa", RadspaPacsReceiver::new, 2, 0),
                new Facility("bhagodaya", "Puru PACS", PuruPacsReceiver::new, 1, 0));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("facilities")
    public void runFacilityOru(Facility facility) {
        String raw = load(facility);
        List<String> messages = Hl7MessageNormalizer.splitMessages(raw);
        log.info("[{}] ORU execution started. pacsName={} file={} messages={}",
                facility.name, facility.pacsName, facility.fileName(), messages.size());
        if (messages.isEmpty()) {
            log.error("[{}] ORU execution FAILED. no MSH messages in {}", facility.name, facility.fileName());
            throw new AssertionError("No HL7 messages for facility " + facility.name);
        }

        AbstractPacsReportReceiver receiver = facility.receiver.get();
        List<PACSDiagnosticsReport> successes = new ArrayList<>();
        List<String> failures = new ArrayList<>();
        for (int i = 0; i < messages.size(); i++) {
            int n = i + 1;
            try {
                PACSDiagnosticsReport report = receiver.processRawHl7(messages.get(i), facility.pacsName);
                successes.add(report);
                log.info("[{}] message {}/{} SUCCESS mrn={} accessionNo={} docName={}",
                        facility.name, n, messages.size(),
                        report.getMrn(), report.getAccessionNo(), report.getDocName());
            } catch (Exception e) {
                failures.add(e.getMessage());
                log.error("[{}] message {}/{} FAIL error={}", facility.name, n, messages.size(), e.getMessage());
            }
        }

        if (failures.isEmpty()) {
            log.info("[{}] ORU execution SUCCESS. pacsName={} success={} fail=0",
                    facility.name, facility.pacsName, successes.size());
        } else if (successes.isEmpty()) {
            log.error("[{}] ORU execution FAILED. pacsName={} success=0 fail={} details={}",
                    facility.name, facility.pacsName, failures.size(), failures);
        } else {
            log.warn("[{}] ORU execution completed with failures. pacsName={} success={} fail={} details={}",
                    facility.name, facility.pacsName, successes.size(), failures.size(), failures);
        }

        assertEquals(facility.expectedTotal, messages.size(), facility.name + ": message count");
        assertEquals(facility.expectedFail, failures.size(),
                facility.name + ": failed messages " + failures);
        assertEquals(facility.expectedTotal - facility.expectedFail, successes.size(),
                facility.name + ": mapped messages");
        successes.forEach(report -> assertTrue(
                report.getObxObservationValue() != null && !report.getObxObservationValue().trim().isEmpty(),
                facility.name + ": empty report body for accessionNo " + report.getAccessionNo()));
    }

    private static String load(Facility facility) {
        Path path = ORU_DIR.resolve(facility.name).resolve(facility.fileName());
        if (Files.isRegularFile(path)) {
            try {
                return Files.readString(path, StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new UncheckedIOException("Unable to read HL7 sample: " + path, e);
            }
        }
        String classpath = "/com/mednet/externalpacs/reports/" + facility.name + "/" + facility.fileName();
        try (InputStream in = PacsOruReportTest.class.getResourceAsStream(classpath)) {
            if (in == null) {
                throw new IllegalArgumentException("Missing HL7 sample: " + path + " (and classpath " + classpath + ")");
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to read HL7 sample: " + classpath, e);
        }
    }

    static final class Facility {

        private final String name;
        private final String pacsName;
        private final Supplier<AbstractPacsReportReceiver> receiver;
        private final int expectedTotal;
        private final int expectedFail;

        Facility(String name, String pacsName, Supplier<AbstractPacsReportReceiver> receiver,
                 int expectedTotal, int expectedFail) {
            this.name = name;
            this.pacsName = pacsName;
            this.receiver = receiver;
            this.expectedTotal = expectedTotal;
            this.expectedFail = expectedFail;
        }

        /** PACS name as the sample file name, e.g. {@code Centricity - GE} to {@code centricityge.txt}. */
        String fileName() {
            return pacsName.toLowerCase().replaceAll("[^a-z0-9]", "") + ".txt";
        }

        @Override
        public String toString() {
            return name + " (" + pacsName + ")";
        }
    }
}
