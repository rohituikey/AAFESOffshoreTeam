///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.aafes.starsettler.control;
//
//import com.aafes.starsettler.gateway.fdms.FirstDataSettler;
//import com.aafes.starsettler.gateway.vision.VisionSettler;
//import static org.junit.Assert.assertEquals;
//import org.junit.Test;
//
///**
// *
// * @author ghadiyamp
// */
//public class SettleFactoryTest {
//    
//     @Test
//    public void testfindSettler() {
//        
//        String settlerType = "fdms";
//        String settlerType1 = "milstar";
//        String settlerType2 = "none";
//        
//        SettleFactory factory = new SettleFactory();
//         FirstDataSettler firstDataSettler = new FirstDataSettler();
//         VisionSettler visionSettler = new VisionSettler();
//        factory.findSettler(settlerType);
//        factory.findSettler(settlerType1);
//        factory.findSettler(settlerType2);
//        factory.setFirstDataSettler(firstDataSettler);
//        factory.setVisionSettler(visionSettler);
//         assertEquals(true, true);
//        
//    }
//}
