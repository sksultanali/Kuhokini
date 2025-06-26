package com.kuhokini.DeliveryModels;

import java.util.List;
import java.util.Map;

public class InvoiceResponse {
    private String status;
    private String zone;
    private double charge_DL;
    private double charge_RTO;
    private double charge_DTO;
    private double charge_COD;
    private double charge_FS;
    private double charge_CNC;
    private double charge_AWB;
    private double charge_ROV;
    private double charge_INS;
    private double charge_FOD;
    private double charge_MPS;
    private double charge_WOD;
    private double gross_amount;
    private double total_amount;
    private double charge_CCOD;
    private double charge_POD;
    private double charge_COVID;
    private double charge_FSC;
    private double charge_FOV;
    private double charge_pickup;
    private double charged_weight;
    private Object zonal_cl;
    private Object wt_rule_id;
    private double charge_AIR;
    private double charge_LABEL;
    private double charge_DEMUR;
    private double charge_REATTEMPT;
    private double charge_DOCUMENT;
    private double charge_DPH;
    private double charge_QC;
    private double charge_CWH;
    private double charge_E2E;
    private double charge_LM;
    private Map<String, Object> adhoc_data;
    private TaxData tax_data;

    // ✅ isSuccessful method
    public boolean isSuccessful() {
        // A simple check — if status is not null or empty, we consider it a success
        return status != null && !status.isEmpty();
    }

    public static class TaxData {
        private double SGST;
        private double CGST;
        private double IGST;
        private double service_tax;
        private double swacch_bharat_tax;
        private double krishi_kalyan_cess;

        // Getters and Setters
        public double getSGST() { return SGST; }
        public void setSGST(double SGST) { this.SGST = SGST; }

        public double getCGST() { return CGST; }
        public void setCGST(double CGST) { this.CGST = CGST; }

        public double getIGST() { return IGST; }
        public void setIGST(double IGST) { this.IGST = IGST; }

        public double getService_tax() { return service_tax; }
        public void setService_tax(double service_tax) { this.service_tax = service_tax; }

        public double getSwacch_bharat_tax() { return swacch_bharat_tax; }
        public void setSwacch_bharat_tax(double swacch_bharat_tax) { this.swacch_bharat_tax = swacch_bharat_tax; }

        public double getKrishi_kalyan_cess() { return krishi_kalyan_cess; }
        public void setKrishi_kalyan_cess(double krishi_kalyan_cess) { this.krishi_kalyan_cess = krishi_kalyan_cess; }
    }

    // Only a few getters/setters shown here for brevity
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public TaxData getTax_data() { return tax_data; }
    public void setTax_data(TaxData tax_data) { this.tax_data = tax_data; }

    public String getZone() {
        return zone;
    }

    public double getCharge_DL() {
        return charge_DL;
    }

    public double getCharge_RTO() {
        return charge_RTO;
    }

    public double getCharge_DTO() {
        return charge_DTO;
    }

    public double getCharge_COD() {
        return charge_COD;
    }

    public double getCharge_FS() {
        return charge_FS;
    }

    public double getCharge_CNC() {
        return charge_CNC;
    }

    public double getCharge_AWB() {
        return charge_AWB;
    }

    public double getCharge_ROV() {
        return charge_ROV;
    }

    public double getCharge_INS() {
        return charge_INS;
    }

    public double getCharge_FOD() {
        return charge_FOD;
    }

    public double getCharge_MPS() {
        return charge_MPS;
    }

    public double getCharge_WOD() {
        return charge_WOD;
    }

    public double getGross_amount() {
        return gross_amount;
    }

    public double getTotal_amount() {
        return total_amount;
    }

    public double getCharge_CCOD() {
        return charge_CCOD;
    }

    public double getCharge_POD() {
        return charge_POD;
    }

    public double getCharge_COVID() {
        return charge_COVID;
    }

    public double getCharge_FSC() {
        return charge_FSC;
    }

    public double getCharge_FOV() {
        return charge_FOV;
    }

    public double getCharge_pickup() {
        return charge_pickup;
    }

    public double getCharged_weight() {
        return charged_weight;
    }

    public Object getZonal_cl() {
        return zonal_cl;
    }

    public Object getWt_rule_id() {
        return wt_rule_id;
    }

    public double getCharge_AIR() {
        return charge_AIR;
    }

    public double getCharge_LABEL() {
        return charge_LABEL;
    }

    public double getCharge_DEMUR() {
        return charge_DEMUR;
    }

    public double getCharge_REATTEMPT() {
        return charge_REATTEMPT;
    }

    public double getCharge_DOCUMENT() {
        return charge_DOCUMENT;
    }

    public double getCharge_DPH() {
        return charge_DPH;
    }

    public double getCharge_QC() {
        return charge_QC;
    }

    public double getCharge_CWH() {
        return charge_CWH;
    }

    public double getCharge_E2E() {
        return charge_E2E;
    }

    public double getCharge_LM() {
        return charge_LM;
    }

    public Map<String, Object> getAdhoc_data() {
        return adhoc_data;
    }
}


