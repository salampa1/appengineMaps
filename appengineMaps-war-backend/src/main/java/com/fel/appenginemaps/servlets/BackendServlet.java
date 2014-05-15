package com.fel.appenginemaps.servlets;

import com.fel.bond.grids.TimeGrid;
import com.fel.bond.intel.IntelProvider;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.utils.SystemProperty;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BackendServlet extends HttpServlet {
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        doPost(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("text/plain");
        resp.getWriter().println("This is a backend instance. \n\n");

        if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
            resp.getWriter().println("This is response from app engine. \n\n");
        } else {
            resp.getWriter().println("This is response from local. \n\n");
        }
        String parameter = req.getParameter("smugglers");
        
        int smugglers = Integer.parseInt(parameter);

        TimeGrid t = IntelProvider.generateIntel(smugglers);
        
        // now add t in payload
        

        Queue queue = QueueFactory.getQueue("pullqueue");

        TaskOptions to = TaskOptions.Builder.withMethod(TaskOptions.Method.PULL).payload("hello world");

        queue.add(to);
        resp.getWriter().println("Added to pullqueue! \n\n");




    }
}