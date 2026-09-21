package com.mednet.externalpacs.reports.dto.receive;

/**
 * DTO for saving a PACS diagnostic report into Mednet.
 */
public class PACSDiagnosticsReport {

    private String mrn;
    private String accessionNo;
    private String medicalLicenceNumberOrEmpCode;
    private String docName;
    private String obxObservationValue;
    private String technicianCode;

    public String getMrn() {
        return mrn;
    }

    public void setMrn(String mrn) {
        this.mrn = mrn;
    }

    public String getAccessionNo() {
        return accessionNo;
    }

    public void setAccessionNo(String accessionNo) {
        this.accessionNo = accessionNo;
    }

    public String getMedicalLicenceNumberOrEmpCode() {
        return medicalLicenceNumberOrEmpCode;
    }

    public void setMedicalLicenceNumberOrEmpCode(String medicalLicenceNumberOrEmpCode) {
        this.medicalLicenceNumberOrEmpCode = medicalLicenceNumberOrEmpCode;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getObxObservationValue() {
        return obxObservationValue;
    }

    public void setObxObservationValue(String obxObservationValue) {
        this.obxObservationValue = obxObservationValue;
    }

    public String getTechnicianCode() {
        return technicianCode;
    }

    public void setTechnicianCode(String technicianCode) {
        this.technicianCode = technicianCode;
    }

    @Override
    public String toString() {
        return "PACSDiagnosticsReport{"
                + "mrn='" + mrn + '\''
                + ", accessionNo='" + accessionNo + '\''
                + ", medicalLicenceNumberOrEmpCode='" + medicalLicenceNumberOrEmpCode + '\''
                + ", docName='" + docName + '\''
                + ", technicianCode='" + technicianCode + '\''
                + ", obxObservationValueLength="
                + (obxObservationValue == null ? 0 : obxObservationValue.length())
                + '}';
    }
}
