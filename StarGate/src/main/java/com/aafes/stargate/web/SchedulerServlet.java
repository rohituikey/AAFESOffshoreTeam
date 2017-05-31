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
import javax.servlet.annotation.WebServlet;

/**
 *
 * @author alugumetlas
 */
@WebServlet(name = "SchedulerServlet", urlPatterns = {"/SchedulerServlet"})
public class SchedulerServlet implements ServletContextListener {

    ServletContext servletContext;

    public void contextInitialized(ServletContextEvent sce) {
        servletContext = sce.getServletContext();
        Timer time = new Timer();
        Scheduler scheduler = new Scheduler();
        time.schedule(scheduler, 0, 10000);

    }

    public void contextDestroyed(ServletContextEvent sce) {
    }
}
