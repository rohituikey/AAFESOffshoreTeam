/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.starsettler.control;

import com.aafes.starsettler.gateway.fdms.FirstDataSettler;
import com.aafes.starsettler.gateway.retailer.RetailSettler;
import com.aafes.starsettler.gateway.vision.VisionSettler;
import com.aafes.starsettler.util.SettlerType;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author ganjis
 */
@Stateless
public class SettleFactory {
    
    @EJB
    private FirstDataSettler firstDataSettler;
    @EJB
    private VisionSettler visionSettler;
    @EJB
    private RetailSettler retailSettler;
   
    
    public BaseSettler findSettler(String settlerType) {

        switch (settlerType) {
            case SettlerType.FDMS:
                return firstDataSettler;

            case SettlerType.MILSTAR:
                return visionSettler;
                
            case SettlerType.DECA:
                return retailSettler;
            // Add more settlers
            default:
                return null;
        }

    }

    /**
     * @param firstDataSettler the firstDataSettler to set
     */
    public void setFirstDataSettler(FirstDataSettler firstDataSettler) {
        this.firstDataSettler = firstDataSettler;
    }

    /**
     * @param visionSettler the visionSettler to set
     */
    public void setVisionSettler(VisionSettler visionSettler) {
        this.visionSettler = visionSettler;
    }
}
