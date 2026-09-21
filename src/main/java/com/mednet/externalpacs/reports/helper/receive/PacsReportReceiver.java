package com.mednet.externalpacs.reports.helper.receive;

import com.mednet.externalpacs.reports.dto.receive.PACSDiagnosticsReport;

public interface PacsReportReceiver {
    public PACSDiagnosticsReport processRawHl7(String rawHl7, String pacsName) throws Exception;
}
