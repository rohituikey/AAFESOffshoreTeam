/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway;

/**
 *
 * @author joshid
 */
final public class MQGatewayException extends RuntimeException
{
    private char responseType = 'T';
    private String reasonCode = "999"; 
    
    public MQGatewayException(String message, Throwable cause)
    {
        super(message, cause);
    }
    public MQGatewayException(String message)
    {
        super(message);
    }
    public MQGatewayException(String message, char responseType)
    {
        super(message);
        this.responseType = responseType;
    }

    public MQGatewayException(String message, char responseType, String reasonCode)
    {
        super(message); 
        this.responseType = responseType; 
        this.reasonCode = reasonCode; 
    }

//    public char getResponseType()
//    {
//        return responseType;
//    }
//    
//    public String getReasonCode()
//    {
//        return reasonCode; 
//    }
}