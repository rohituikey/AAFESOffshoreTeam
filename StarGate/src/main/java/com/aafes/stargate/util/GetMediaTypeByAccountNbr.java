/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.util;

import com.aafes.stargate.util.MediaType;

/**
 *
 * @author ganjis
 */
public final class GetMediaTypeByAccountNbr {
    private static String Visa = "400000-499999";
    private static String MasterCard = "500000-559999";
    private static String Discover = "601100-601199,650000-659999,622126-622925,644000-649999";
    private static String Amex = "340000-349999,370000-379999";
    private static String MilStar = "601900-601999";

    public static String getCardType(String PrimaryAccountNumber) {
        
        PrimaryAccountNumber = PrimaryAccountNumber.trim().replaceFirst("^0+(?!$)", "").substring(0, 6);
        
        String[] visaRange = GetMediaTypeByAccountNbr.Visa.split("-");
        String[] mastercardRange = GetMediaTypeByAccountNbr.MasterCard.split("-");
        String[] discoverRange = GetMediaTypeByAccountNbr.Discover.split(",");
        String[] amexRange = GetMediaTypeByAccountNbr.Amex.split(",");
        String[] milstarRange = GetMediaTypeByAccountNbr.MilStar.split("-");
        try {

            if (Integer.parseInt(PrimaryAccountNumber) >= Integer.parseInt(visaRange[0])
                    && Integer.parseInt(PrimaryAccountNumber) <= Integer.parseInt(visaRange[1])) {
                return MediaType.VISA;
            } else if (Integer.parseInt(PrimaryAccountNumber) >= Integer.parseInt(mastercardRange[0])
                    && Integer.parseInt(PrimaryAccountNumber) <= Integer.parseInt(mastercardRange[1])) {
                return MediaType.MASTER;
            } else if (Integer.parseInt(PrimaryAccountNumber) >= Integer.parseInt(milstarRange[0])
                    && Integer.parseInt(PrimaryAccountNumber) <= Integer.parseInt(milstarRange[1])) {
                return MediaType.MIL_STAR;
            } else {
                for (int i = 0; i < discoverRange.length; i++) {
                    String[] discoverTmp = discoverRange[i].split("-");
                    if (Integer.parseInt(PrimaryAccountNumber) >= Integer.parseInt(discoverTmp[0])
                            && Integer.parseInt(PrimaryAccountNumber) <= Integer.parseInt(discoverTmp[1])) {
                        return MediaType.DISCOVER;
                    }
                }

                for (int i = 0; i < amexRange.length; i++) {
                    String[] amexTmp = amexRange[i].split("-");
                    if (Integer.parseInt(PrimaryAccountNumber) >= Integer.parseInt(amexTmp[0])
                            && Integer.parseInt(PrimaryAccountNumber) <= Integer.parseInt(amexTmp[1])) {
                        return MediaType.AMEX;
                    }
                }
            }
        } catch (NullPointerException ex) {
            return null;
        }

        return null;
    }
}
