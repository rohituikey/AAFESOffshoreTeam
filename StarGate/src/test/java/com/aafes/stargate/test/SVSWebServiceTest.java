///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.aafes.stargate.test;
//
//import com.svs.svsxml.beans.Amount;
//import com.svs.svsxml.beans.BalanceInquiryRequest;
//import com.svs.svsxml.beans.BalanceInquiryResponse;
//import com.svs.svsxml.beans.Card;
//import com.svs.svsxml.beans.Merchant;
//import com.svs.svsxml.service.SVSXMLWay;
//import com.svs.svsxml.service.SVSXMLWayService;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.Map;
//import javax.xml.namespace.QName;
//import javax.xml.ws.BindingProvider;
//
///**
// *
// * @author pkalpesh
// */
//public class SVSWebServiceTest {
//
//    public static void main(String args[]) throws MalformedURLException {
//
//        System.setProperty("https.proxyHost", "hqproxyguest1.aafes.com");
//        System.setProperty("https.proxyPort", "9090");
//
//        SVSXMLWayService sVSXMLWayService = new SVSXMLWayService();
//
//       
//        SVSXMLWay sVSXMLWay = sVSXMLWayService.getSVSXMLWay();
//       
//
//        Map<String, Object> requestContext
//                = ((BindingProvider) sVSXMLWay).getRequestContext();
//        requestContext.put(BindingProvider.USERNAME_PROPERTY, "extspeedfcuat");
//        requestContext.put(BindingProvider.PASSWORD_PROPERTY, "Rc464Fc14");
//
//        BalanceInquiryRequest balanceInquiryRequest = new BalanceInquiryRequest();
//
//        Amount amount = new Amount();
//        amount.setAmount(0.00);
//        amount.setCurrency("USD");
//        balanceInquiryRequest.setAmount(amount);
//
//        Card card = new Card();
//        card.setCardCurrency("USD");
//        card.setCardExpiration("");
//        card.setCardNumber("6006496628299904508");
//        card.setCardTrackOne("");
//        card.setCardTrackTwo("");
//        card.setEovDate("");
//        card.setPinNumber("00002496");
//        balanceInquiryRequest.setCard(card);
//
//        Merchant merchant = new Merchant();
//        merchant.setDivision("99999");
//        merchant.setMerchantName("IT-D VP OFFICE");
//        merchant.setMerchantNumber("061571");
//        merchant.setStoreNumber("F00500");
//        balanceInquiryRequest.setMerchant(merchant);
//
//        balanceInquiryRequest.setCheckForDuplicate(false);
//        balanceInquiryRequest.setDate("2017-01-13T15:37:01");
//        balanceInquiryRequest.setInvoiceNumber("9999");
//        balanceInquiryRequest.setRoutingID("6006491571000000000");
//        balanceInquiryRequest.setStan("112233");
//        balanceInquiryRequest.setTransactionID("");
//
//        
//        BalanceInquiryResponse balanceInquiryResponse = sVSXMLWay.balanceInquiry(balanceInquiryRequest);
//        
//
//        System.out.println(balanceInquiryResponse.getAuthorizationCode());
//        System.out.println(balanceInquiryResponse.getReturnCode().getReturnCode());
//        System.out.println(balanceInquiryResponse.getReturnCode().getReturnDescription());
//        
//
//    }
//
//}
