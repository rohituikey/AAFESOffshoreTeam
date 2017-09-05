/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.gateway.wex;

import java.util.Date;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author alugumetlas
 */
public class GenerateLogWexDetailsTest {
    
    @Before
    public void setUp() {
    }
    /**
     * Test of generateDetails method, of class GenerateLogWexDetails.
     */
    @Test
    public void testGenerateDetails() {
            GenerateLogWexDetails.generateDetails(new Date().toString(),"","test for dummy request");
//            GenerateLogWexDetails.generateDetails("\n test for appending response");
    }
    
}
