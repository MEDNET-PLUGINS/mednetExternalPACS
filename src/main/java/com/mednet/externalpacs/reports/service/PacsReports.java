package com.mednet.externalpacs.reports.service;

public interface PacsReports {

    public String receivePacsReport(String rawHl7, String pacsName) throws Exception;

}
