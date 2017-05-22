package com.aafes.tokenizer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.spi.InjectionPoint;
import org.slf4j.LoggerFactory;

@Startup
@Singleton
public class Configurator {

    private static final org.slf4j.Logger LOG
            = LoggerFactory.getLogger(Configurator.class.getSimpleName());

    public Configurator() {
    }

    @PostConstruct
    public void postConstruct() {
        load();
        
    }

    public void put(String key, String value) {
        System.setProperty(key, value);
    }

    public void load() {
        String baseDir = System.getProperty("jboss.server.config.dir");
        String defaultPath = baseDir + "/tokenizer.properties";
        String path = System.getProperty("tokenizer.properties", defaultPath);
        Properties properties = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream(path);
            properties.load(input);
            properties.putAll(System.getProperties());
            System.setProperties(properties);
        } catch (IOException ex) {
            LOG.error(ex.toString());
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    LOG.error(e.toString());
                }
            }
        }
    }

    @javax.enterprise.inject.Produces
    public String getString(InjectionPoint ip) {
        String clssName = ip.getMember().getDeclaringClass().getName();
        String field = ip.getMember().getName();
        String name = String.format(clssName + "." + field);
        return System.getProperty(name);
    }

    public String get(String name) {
        return System.getProperty(name);
    }

    @javax.enterprise.inject.Produces
    public int getInt(InjectionPoint ip) {
        String clssName = ip.getMember().getDeclaringClass().getName();
        String field = ip.getMember().getName();
        String name = String.format(clssName + "." + field);
        String value = System.getProperty(name);
        return Integer.parseInt(value);
    }

    public long getInt(String name) {
        String value = System.getProperty(name);
        return Integer.parseInt(value);
    }

    @javax.enterprise.inject.Produces
    public long getLong(InjectionPoint ip) {
        String clssName = ip.getMember().getDeclaringClass().getName();
        String field = ip.getMember().getName();
        String name = String.format(clssName + "." + field);
        String value = System.getProperty(name);
        return Long.parseLong(value);
    }

    public long getLong(String name) {
        String value = System.getProperty(name);
        return Long.parseLong(value);
    }

    public String getAllKeys() {
        StringBuilder sb = new StringBuilder();
        Enumeration<Object> keys = System.getProperties().keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            sb.append(key).append("\n");
        }
        return sb.toString();
    }
}