/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.retailer;

import com.aafes.starsettler.boundary.CommandSettleResource;
import com.aafes.starsettler.entity.CommandMessage;
import com.aafes.starsettler.util.SettlerType;
import javax.xml.datatype.DatatypeConfigurationException;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author burangir
 */
public class TestRetailer {
    
    CommandSettleResource commandSettleResourceObj = new CommandSettleResource();
    CommandMessage commandMessage = new CommandMessage();
    
     @Before
    public void populateVariables() throws DatatypeConfigurationException {
        commandMessage.setProcessDate("20170112");
        commandMessage.setSettlerType(SettlerType.RETAIL);
     }
    
    @Test
    public void callPostXML(){
        commandSettleResourceObj.postXml(commandMessage);
    }
}