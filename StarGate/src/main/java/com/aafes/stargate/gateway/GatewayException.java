/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway;

/**
 *
 * @author joshid
 */
final public class GatewayException extends RuntimeException
{
    private char responseType = 'T';
    private String reasonCode = "999"; 
    
    public GatewayException(String message, Throwable cause)
    {
        super(message, cause);
    }
    public GatewayException(String message)
    {
        super(message);
    }
    public GatewayException(String message, char responseType)
    {
        super(message);
        this.responseType = responseType;
    }

    public GatewayException(String message, char responseType, String reasonCode)
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