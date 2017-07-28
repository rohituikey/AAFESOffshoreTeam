/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.boundary;

import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author mercadoch
 */
public class BasicAuthenticationHeader {

    private String authorization;

    BasicAuthenticationHeader(String authorization) {
        this.authorization = authorization;
    }

    public String getUser() {
        String user = null;
        if (authorization != null && authorization.startsWith("Basic")) {
            String credentials = authorization.substring("Basic".length()).
                    trim();
            byte[] decoded = DatatypeConverter.parseBase64Binary(credentials);
            String decodedString = new String(decoded);
            String[] actualCredentials = decodedString.split(":");
            user = actualCredentials[0];
        }
        return user;
    }

    public String getPassword() {
        String password = null;
        if (authorization != null && authorization.startsWith("Basic")) {
            String credentials = authorization.substring("Basic".length()).
                    trim();
            byte[] decoded = DatatypeConverter.parseBase64Binary(credentials);
            String decodedString = new String(decoded);
            String[] actualCredentials = decodedString.split(":");
            password = actualCredentials[1];
        }
        return password;
    }
}
