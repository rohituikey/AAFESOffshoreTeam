package com.aafes.starsettler.boundary;

import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Add REST resources.
 *
 * @author ganjis
 */
@ApplicationPath("1")
final public class ApplicationConfig extends Application {

    /**
     * Generated framework code for doing RESTeasy.
     *
     * @return
     */
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(com.aafes.starsettler.boundary.SettleMessageResource.class);
        resources.add(com.aafes.starsettler.boundary.TestFDMSXSD.class);
    }

}
