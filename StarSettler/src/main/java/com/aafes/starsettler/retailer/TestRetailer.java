/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.retailer;

import com.aafes.starsettler.boundary.CommandSettleResource;
import com.aafes.starsettler.entity.CommandMessage;
import com.aafes.starsettler.util.SettlerType;

/**
 *
 * @author burangir
 */
public class TestRetailer {
    
    public static void main(String args[]){
        CommandSettleResource obj = new CommandSettleResource();
        CommandMessage commandMessage = new CommandMessage();
        commandMessage.setProcessDate("20170112");
        commandMessage.setSettlerType(SettlerType.RETAIL);
        
        obj.postXml(commandMessage);
    }
    
}
