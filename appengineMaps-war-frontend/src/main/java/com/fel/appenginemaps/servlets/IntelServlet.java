package com.fel.appenginemaps.servlets;

import com.fel.appenginemaps.intel.IntelProvider;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TransientFailureException;
import com.google.appengine.api.utils.SystemProperty;
import com.google.apphosting.api.ApiProxy.ApiDeadlineExceededException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IntelServlet extends HttpServlet {
    
    static int TASKS = 70;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        
        long t = System.currentTimeMillis();

        resp.setContentType("text/plain");
        resp.getWriter().println("Hello, this is a testing servlet. \n\n");

        IntelProvider ip = new IntelProvider();
        ip.provideIntel(TASKS, resp);
        
        resp.getWriter().println("All tasks were put back together, epic win. \n\n");
        
        resp.getWriter().println((System.currentTimeMillis() - t)/1000d +" seconds." );


    }
}