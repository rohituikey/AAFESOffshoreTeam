/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aafes.stargate.web;

import com.aafes.stargate.gateway.svs.Scheduler;
import java.util.Timer;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.slf4j.LoggerFactory;


/**
 *
 * @author alugumetlas
 */
@WebListener
public class SchedulerServlet implements ServletContextListener {
   private static final org.slf4j.Logger log
            = LoggerFactory.getLogger(SchedulerServlet.class.getSimpleName());
    ServletContext servletContext;

    /**
     *
     * @param sce
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("$$$$$$$$****** started context intilized method");
        servletContext = sce.getServletContext();
        Timer time = new Timer();
        Scheduler scheduler = new Scheduler();
        time.schedule(scheduler, 0, 10000);

    }

    public void contextDestroyed(ServletContextEvent sce) {
    }
}
