package com.mednet.externalpacs.reports.controller;

import com.mednet.externalpacs.common.util.APIErrorCodes;
import com.mednet.externalpacs.common.util.APIResponseEntityUtil;
import com.mednet.externalpacs.reports.dto.sendToMednet.ReportHeader;
import com.mednet.externalpacs.reports.service.PacsReports;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/report")
@RequiredArgsConstructor
public class PacsReportController {

    private final PacsReports pacsReports;

    @PostMapping(value = "/receive/{pacsName}",
            consumes = {MediaType.TEXT_PLAIN_VALUE, "application/hl7-v2", "application/hl7-v2+er7", MediaType.ALL_VALUE})
    public ResponseEntity<String> receiveRawHl7Report(@PathVariable("pacsName") String pacsName,
                                                      @RequestBody String rawHl7) throws Exception {
        try {
            if (rawHl7 == null || rawHl7.isBlank()) {
                return APIResponseEntityUtil.createErrorResponseEntity(
                        APIErrorCodes.ERR_CODE_BAD_REQUEST, APIErrorCodes.ERR_MSG_INVALID_INPUT_DATA);
            }
            String response = pacsReports.receivePacsReport(rawHl7, pacsName);
            log.info("[{}] receive API SUCCESS", pacsName);
            return APIResponseEntityUtil.createSuccessResponseEntity(response);
        } catch (Exception e) {
            log.error("[{}] receive API FAIL error={}", pacsName, e.getMessage(), e);
            return APIResponseEntityUtil.createErrorResponseEntity(
                    APIErrorCodes.ERR_CODE_SOMETHING_WENT_WRONG,
                    e.getMessage());
        }
    }

    @GetMapping("/mednet-report-structure")
    public String pullReportPayload() {
        return new ReportHeader().toString();
    }
}
