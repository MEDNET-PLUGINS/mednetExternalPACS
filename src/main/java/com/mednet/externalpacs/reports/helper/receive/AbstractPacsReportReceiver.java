package com.mednet.externalpacs.reports.helper.receive;

import com.mednet.externalpacs.common.util.MednetUtils;
import com.mednet.externalpacs.hl7.Hl7MessageNormalizer;
import com.mednet.externalpacs.reports.dto.receive.PACSDiagnosticsReport;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class AbstractPacsReportReceiver {

    @Value("${mednet.lab.internal.url:}")
    private String mednetLabInternalUrl;

    public final void processReportData(String rawHl7, String pacsName) throws Exception {
        List<String> messages = Hl7MessageNormalizer.splitMessages(rawHl7);
        if (messages.isEmpty()) {
            log.error("[{}] ORU execution FAILED. no MSH messages in payload", pacsName);
            throw new Exception("raw HL7 has no MSH messages");
        }
        log.info("[{}] ORU execution started. messages={}", pacsName, messages.size());
        int success = 0;
        List<String> failures = new ArrayList<>();
        for (int i = 0; i < messages.size(); i++) {
            int n = i + 1;
            try {
                PACSDiagnosticsReport pacsReport = processRawHl7(messages.get(i), pacsName);
                log.info("[{}] message {}/{} SUCCESS mrn={} accessionNo={} docName={}",
                        pacsName, n, messages.size(),
                        pacsReport == null ? null : pacsReport.getMrn(),
                        pacsReport == null ? null : pacsReport.getAccessionNo(),
                        pacsReport == null ? null : pacsReport.getDocName());
                sendToMednet(pacsName, pacsReport);
                success++;
            } catch (Exception e) {
                failures.add("message " + n + ": " + e.getMessage());
                log.error("[{}] message {}/{} FAIL error={}", pacsName, n, messages.size(), e.getMessage(), e);
            }
        }
        if (failures.isEmpty()) {
            log.info("[{}] ORU execution SUCCESS. success={} fail=0", pacsName, success);
        } else {
            log.error("[{}] ORU execution FAILED. success={} fail={} details={}",
                    pacsName, success, failures.size(), failures);
            throw new Exception(pacsName + " ORU execution failed for " + failures.size()
                    + " of " + messages.size() + " messages: " + failures);
        }
    }

    public abstract PACSDiagnosticsReport processRawHl7(String rawHl7, String pacsName) throws Exception;


    public void sendToMednet(String pacsName, PACSDiagnosticsReport pacsReport) throws Exception {
        if (pacsReport == null) {
            return;
        }
        invokeMednetAPI(pacsName, pacsReport);
    }

    private void invokeMednetAPI(String pacsName, PACSDiagnosticsReport pacsReport) throws Exception {
        if (StringUtils.isBlank(mednetLabInternalUrl)) {
            throw new Exception("mednet.lab.internal.url is not configured");
        }
        String payload = MednetUtils.createConfiguredMapper().writeValueAsString(pacsReport);
        String finalUrl = StringUtils.removeEnd(mednetLabInternalUrl.trim(), "/") + "/api/v1/pacsreport/process";

        Client client = null;
        try {
            client = Client.create();
            client.setConnectTimeout(15000);
            client.setReadTimeout(60000);

            log.info("[{}] sendToMednet URL={} accessionNo={}", pacsName, finalUrl, pacsReport.getAccessionNo());

            String jsonUtf8 = MediaType.APPLICATION_JSON + ";charset=UTF-8";
            byte[] utf8Body = payload.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            WebResource.Builder builder = client.resource(finalUrl)
                    .accept(jsonUtf8)
                    .type(jsonUtf8)
                    .header("mednetOAuthApiToken", "607e2bb49932eedc15e24b0694dd7556732c909d")
                    .header("loggedInUserID", "-1");

            ClientResponse response = builder.post(ClientResponse.class, utf8Body);
            int statusCode = response.getStatus();
            String responseBody = response.getEntity(String.class);
            if (statusCode < 200 || statusCode >= 300) {
                log.error("[{}] sendToMednet FAIL accessionNo={} status={} body={}",
                        pacsName, pacsReport.getAccessionNo(), statusCode, responseBody);
                throw new Exception("Lab /api/v1/pacsreport/process failed with status " + statusCode + " : " + responseBody);
            }
            log.info("[{}] sendToMednet SUCCESS accessionNo={} status={}",
                    pacsName, pacsReport.getAccessionNo(), statusCode);
        } finally {
            if (client != null) {
                client.destroy();
            }
        }
    }
}
