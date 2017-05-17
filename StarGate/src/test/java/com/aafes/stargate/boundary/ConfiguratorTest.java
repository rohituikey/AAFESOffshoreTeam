/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.boundary;


import com.aafes.stargate.control.Configurator;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author pkalpesh
 */
public class ConfiguratorTest {
    
    public ConfiguratorTest() {
    }

    @Test
    public void testLoad() throws Exception { 
        System.setProperty("jboss.server.config.dir", "src/main/resources");
        Configurator configurator = new Configurator();
        configurator.postConstruct();
        configurator.put("Test", "TestValue");
        assertEquals("ECOAUTHI", configurator.get("com.aafes.stargate.gateway.vision.entity.CICSHandlerBean.cicsMQProgName"));
    }
}
