package com.fel.appenginemaps.servlets;

import com.google.appengine.api.utils.SystemProperty;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IntelServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        
        
        resp.setContentType("text/plain");
        resp.getWriter().println("Hello, this is a testing servlet. \n\n");
        
        if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
            resp.getWriter().println("This is response from app engine. \n\n");
        } else {
            resp.getWriter().println("This is response from local. \n\n");
        }
        
        Properties p = System.getProperties();
        p.list(resp.getWriter());
       
        
    }
}