/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.test;

import com.aafes.stargate.authorizer.entity.Transaction;
import com.aafes.stargate.stub.CIDValidationStub;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author singha
 */

public class CIDValidationStubTest {
    
    private  Transaction transaction = new Transaction();
    
    @Test
    public void testCidValodationApproved()
    {
       transaction.setComment("VALID_APPROVED");
        Assert.assertEquals(true,  CIDValidationStub.validateStub(transaction));
    }
    
    @Test
    public void testCidValodationInvalidApprove()
    {
       transaction.setComment("INVALID_DECLINE");
        Assert.assertEquals(false,  CIDValidationStub.validateStub(transaction));
    }
    
    
     @Test
    public void testCidValodationValidDecilne()
    {
       transaction.setComment("INVALID_APPROVED");
        Assert.assertEquals(false,  CIDValidationStub.validateStub(transaction));
    }
    
    @Test
    public void testCidValodationDecilne()
    {
       transaction.setComment("VALID_DECLINE");
        Assert.assertEquals(true,  CIDValidationStub.validateStub(transaction));
    }
    
    
    
}
