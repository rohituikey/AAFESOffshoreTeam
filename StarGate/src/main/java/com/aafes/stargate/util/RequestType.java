package com.aafes.stargate.util;


final public class RequestType {

    public static final String SALE = "Sale";
    public static final String ACTIVATE = "Activate";
    public static final String INQUIRY = "Inquiry";
    public static final String LOAD = "Load";
    public static final String REFUND = "Refund";
    public static final String REVERSAL = "Reversal";
    public static final String PAYMENT = "Payment";
    public static final String SETTLE = "Settle";
    public static final String PREAUTH = "PreAuth";
    public static final String FINAL = "Final";
    public static final String FINAL_AUTH = "FinalAuth";
    public static final String ISSUE = "Issue";
    //added below two lines
    public static final String NETWORK = "Network";
    public static final String REDEMPTION = "redemption";


    private RequestType() {
        // Never instantiate.
    }
}
