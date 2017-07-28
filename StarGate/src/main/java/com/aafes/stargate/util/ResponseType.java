package com.aafes.stargate.util;

final public class ResponseType {

    private ResponseType() {
        // Never instantiate.
    }
    public static final String APPROVED = "Approved";
    public static final String DECLINED = "Decline";
    public static final String TIMEOUT = "TIMEOUT";
    public static final String REFERRAL = "Referral";
    public static final String PENDING = "Pending";
    public static final String CALLCREDIT = "CALLCREDIT";
    public static final String SendToService = "SendToService";

}
