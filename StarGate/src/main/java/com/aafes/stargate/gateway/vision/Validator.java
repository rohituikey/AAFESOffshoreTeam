/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.vision;

/**
 *
 * @author nguyentul
 */
final public class Validator {

    public static boolean isLocationId(String locationId) {
        if ((locationId.length() > 0 && locationId.length() < 10) && locationId.matches("\\d{1,10}")) {
            return true;
        } else {
            return false;
        }

    }

    public static boolean isAmount(String amount) {
        if ((amount.length() > 0 && amount.length() < 8) && amount.matches("^[0-9]{1,5}[.]{1}[0-9]{1,2}$")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isSignAmount(String amount) {
        if (amount.length() == 1 && amount.matches("[1-9]$")) {
            return true;
        } else if ((amount.length() > 1 && amount.length() <= 8) && amount.matches("^[0-9-]{1}[0-9]{1,7}$")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isMenuId(String menuId) {

        if ((menuId.length() > 0 && menuId.length() < 10) && menuId.matches("\\d{1,10}")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isSessionId(String sessionId) {
        if ((sessionId.length() > 10 && sessionId.length() < 40) && sessionId.matches("^[a-zA-Z0-9-]+$")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isTraceId(String sessionId) {
        if (sessionId != null) {
            if ((sessionId.length() > 10 && sessionId.length() < 40) && sessionId.matches("^[a-zA-Z0-9-]+$")) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean isCreditCard(String cardNumber) {
        if (cardNumber == null || cardNumber.length() == 0) {
            return false;
        } else {
            if (cardNumber.matches("\\d{13,20}")) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static boolean isCVV(String cvv) {
        if (cvv != null && cvv.matches("\\d{3,4}")) {
            return true;
        } else {
            return false;

        }
    }

    public static boolean isZipCode(String zipcode) {
        if (zipcode != null && zipcode.matches("\\d{5,9}")) {
            return true;
        } else {
            return false;
        }

    }

    public static boolean isExp(String expMonth, String expYear) {
        if (expMonth.matches("\\d{2}") && expYear.matches("\\d{2}")) {
            return true;
        } else {
            return false;
        }

    }

    public static boolean isExp(String Exp) {
        if (Exp.matches("[1-4][0-9][0-1][0-9]")) {
            return true;
        } else {
            return false;
        }

    }

    public static boolean isAuthCode(String authorizationCode) {
        if (authorizationCode.trim().matches("[a-zA-Z0-9\\s]{3,8}")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isResponseCode(String rspCode) {
        if (rspCode.matches("^[A-Z0-9]{1,3}")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isMilStar(String isMilStar) {
        if (isMilStar.matches("(true|false)")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isNumberOnly(String NumberOnly) {
        if (NumberOnly.matches("\\d{1,12}")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isSettleIndicator(boolean SettleIndicator) {
        if (SettleIndicator == true || SettleIndicator == false) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isInputType(String inputType) {
        if (inputType != null) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isRequestType(String requestType) {
        if (requestType != null) {
            return true;
        } else {
            return false;
        }
    }
}
