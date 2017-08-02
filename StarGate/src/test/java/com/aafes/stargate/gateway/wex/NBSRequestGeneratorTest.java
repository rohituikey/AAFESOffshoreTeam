/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex;
import com.aafes.nbslogonrequestschema.NbsLogonRequest;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author uikuyr
 */
public class NBSRequestGeneratorTest {
    
    NbsLogonRequest nbsLogonRequest;
    NBSRequestGenerator subjectUnderTest = new NBSRequestGenerator();
    
    @Before
    public void setUp() {
        nbsLogonRequest = new NbsLogonRequest();
        NbsLogonRequest.HeaderRecord headerRecord = new NbsLogonRequest.HeaderRecord();
        NbsLogonRequest.HeaderRecord.CardSpecificData cardSpecificData = new NbsLogonRequest.HeaderRecord.CardSpecificData();
//        NbsLogonRequest.HeaderRecord.CardSpecificData.WexProductDetails wexProductDetails = new NbsLogonRequest.HeaderRecord.CardSpecificData.WexProductDetails();
//        NbsLogonRequest.HeaderRecord.CardSpecificData.WexPromptDetails wexPromptDetails= new NbsLogonRequest.HeaderRecord.CardSpecificData.WexPromptDetails();
//        NbsLogonRequest.HeaderRecord.CardSpecificData.WexProductDetails.ProdDetailCount prodDetailCount = new NbsLogonRequest.HeaderRecord.CardSpecificData.WexProductDetails.ProdDetailCount();
//        NbsLogonRequest.HeaderRecord.CardSpecificData.WexPromptDetails.PromptDetailCount promptDetailCount= new NbsLogonRequest.HeaderRecord.CardSpecificData.WexPromptDetails.PromptDetailCount();
        
        cardSpecificData.setAcctInfo("dummy Info");
        cardSpecificData.setAmount(BigDecimal.ONE);
        
        headerRecord.setA("dummy Info");
        headerRecord.setCATFlag(BigInteger.ONE);
        headerRecord.setCardType("dummy Info");
        headerRecord.setKey(BigInteger.ONE);
        headerRecord.setPumpNo("dummy Info");
        headerRecord.setServiceType("S");
        headerRecord.setTrack(BigInteger.ONE);
        headerRecord.setTransType(BigInteger.ONE);
        headerRecord.setCardSpecificData(cardSpecificData);
        
        nbsLogonRequest.setAppName("dummy Appname");
        nbsLogonRequest.setAppVersion(new BigInteger("1"));
        nbsLogonRequest.setTermId("dummy Term ID");
        nbsLogonRequest.setTimeZone(new BigInteger("02501"));
        nbsLogonRequest.setHeaderRecord(headerRecord);
        
        ISOMsg request = new ISOMsg();
        try {
            GenericPackager packager = new GenericPackager("src/main/resources/xml/NBSLogonPackager.xml");
            request.setPackager(packager);
        } catch (ISOException ex) {
            Logger.getLogger(NBSRequestGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        subjectUnderTest.setRequest(request);
    }
    

    @Test
    public void testGenerateLogOnPacketRequest() {
        System.out.println("generateLogOnPacketRequest");
        
        NBSRequestGenerator instance = new NBSRequestGenerator();
        String expResult = "";
        String result = instance.generateLogOnPacketRequest(nbsLogonRequest);
        //assertEquals(expResult, result);
    }

   
    
}
