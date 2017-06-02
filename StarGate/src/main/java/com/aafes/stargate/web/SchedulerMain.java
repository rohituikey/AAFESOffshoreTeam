/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.web;

import com.aafes.stargate.gateway.svs.Scheduler;
import java.util.Timer;

/**
 *
 * @author alugumetlas
 */
public class SchedulerMain {
    public static  void main(String args[])
    {
        System.out.println("com.aafes.stargate.web.SchedulerMain.main()");
         Timer time = new Timer();
        Scheduler scheduler = new Scheduler();
        time.schedule(scheduler, 0, 10000);
    }
    
}
