/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex.simulator;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.control.Configurator;
import com.aafes.stargate.util.InputType;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author uikuyr
 */
public class NBSFormatterFSTest {
    private NBSFormatterFS subjectUnderTest;
    Transaction transaction;
    
    @Before
    public void setUp() {
        subjectUnderTest = new NBSFormatterFS();
        transaction=new Transaction();
        transaction.setTermId("01");
        transaction.setTransactionId("01000");
        transaction.setCatFlag("N");
        transaction.setPumpNmbr("00");
        transaction.setAmount(12);
        transaction.setAmtPreAuthorized(12);
        transaction.setAuthNumber("23");
        transaction.setTrack2("123456");
        transaction.setInputType(InputType.SWIPED);
    }
    
    @Test
    public void testCreatePreAuthRequestForNBS() {
        String result = subjectUnderTest.createPreAuthRequestForNBS(transaction);
        assertEquals("", result);
    }

    @Ignore
    @Test
    public void testCreateFinalRequestForNbs() {
        System.out.println("createFinalRequestForNbs");
        Transaction t = null;
        NBSFormatterFS instance = new NBSFormatterFS();
        String expResult = "";
        String result = instance.createFinalRequestForNbs(t);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }
    
}
