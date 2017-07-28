/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.control;

/**
 *
 * @author ganjis
 */
public class AuthorizerException extends RuntimeException
{   
    public AuthorizerException(String message)
    {
        super(message);
    }
}