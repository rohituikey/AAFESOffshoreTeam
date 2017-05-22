/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.tokenservice.util;

/**
 *
 * @author ganjis
 */
public final class ResponseType {
    private ResponseType() {
        // Never instantiate.
    }
    public static final String SUCCESS = "SUCCESS";
    public static final String FAILED = "FAILED";
    public static final String TIMEOUT = "TIMEOUT";
}
