/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.entity;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author pkalpesh
 */
@XmlRootElement
public class CommandMessage {
    
    
    private String settlerType ="";
    private String processDate="";

    public CommandMessage() {
    }
    
    
    public String getSettlerType() {
        return settlerType;
    }

    public void setSettlerType(String settlerType) {
        this.settlerType = settlerType;
    }

    public String getProcessDate() {
        return processDate;
    }

    public void setProcessDate(String processDate) {
        this.processDate = processDate;
    }
    
    
    
}
