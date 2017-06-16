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
import org.junit.Test;

/**
 *
 * @author singha
 */

public class CIDValidationStubTest {
    
    private  Transaction transaction = new Transaction();
    
    @Before
    public void setup()
    {
        transaction.setComment("Vaild I for Approved");
    }
    
    
    @Test
    public void testCidValodationApproved()
    {
       transaction.setComment("Vaild I for Approved");
        Assert.assertEquals(true,  CIDValidationStub.validateStub(transaction));
    }
    
    @Test
    public void testCidValodationInvalidApprove()
    {
       transaction.setComment("Invalid I for Approve");
        Assert.assertEquals(true,  CIDValidationStub.validateStub(transaction));
    }
    
     @Test
    public void testCidValodationValidDecilne()
    {
       transaction.setComment("valid I for Decline");
        Assert.assertEquals(false,  CIDValidationStub.validateStub(transaction));
    }
    
    @Test
    public void testCidValodationDecilne()
    {
       transaction.setComment("Invalid I for Decilne");
        Assert.assertEquals(false,  CIDValidationStub.validateStub(transaction));
    }
    
   
    
}
