/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex.simulator;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.util.InputType;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author uikuyr
 */
public class NBSFormatterTest {
    
    Transaction t;
    NBSFormatter testSubject;
    
    @Before
    public void setUp() {
        t=new Transaction();
        t.setTermId("01");
        t.setTransactionId("01000");
        t.setCatFlag("N");
        t.setPumpNmbr("00");
        t.setAmount(12);
        t.setAmtPreAuthorized(12);
        t.setAuthNumber("23");
        t.setTrack2("123456");
        t.setInputType(InputType.SWIPED);
        
        testSubject = new NBSFormatter();
    }
    
    @Ignore
    @Test
    public void testCreateRequest() {
        byte[] result = testSubject.createRequest(t);
        String expResult = new String(result);
        NBSClient nBSClient = new NBSClient();
        nBSClient.generateResponse(expResult);
    }

   
    
}
