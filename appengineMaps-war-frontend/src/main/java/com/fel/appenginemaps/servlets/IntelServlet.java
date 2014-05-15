package com.fel.appenginemaps.servlets;

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

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {


        resp.setContentType("text/plain");
        resp.getWriter().println("Hello, this is a testing servlet. \n\n");

        Queue queue;

        if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
            // if we are not app server, use pushqueue that targets backend module
            resp.getWriter().println("This is response from app engine. \n\n");
            queue = QueueFactory.getQueue("pushqueue");
        } else {
            // if we are not local, use default queue that targets this module
            resp.getWriter().println("This is response from local. \n\n");
            queue = QueueFactory.getDefaultQueue();
        }


        TaskOptions to = TaskOptions.Builder.withParam("smugglers", "10").url("/backend").method(TaskOptions.Method.POST);


        int tasks = 10;
        int done = 0;

        //queue.add(to);
        for (int i = 0; i < tasks; i++) {
            queue.add(to);
        }



        Queue pull = QueueFactory.getQueue("pullqueue");;

        while (done < tasks) {
            int sleepDuration = 0;
            List<TaskHandle> leaseTasks = null;

            try {
                leaseTasks = pull.leaseTasks(1, TimeUnit.HOURS, 10); // lease 10 tasks
            } catch (TransientFailureException e) {
                resp.getWriter().println("TransientFailureException: " + e + " \n");
                sleepDuration = 500;
            } catch (ApiDeadlineExceededException e) {
                resp.getWriter().println("ApiDeadlineExceededException: " + e + " \n");
                sleepDuration = 500;
            }
            
            if (leaseTasks != null) {
                if (leaseTasks.isEmpty()) {
                    sleepDuration = 500;
                } else {
                    done += leaseTasks.size();
                    pull.deleteTask(leaseTasks); // deleting done tasks
                }
            }
            
            if (sleepDuration > 0) {
                try {
                    resp.getWriter().println("going to sleep \n");
                    Thread.sleep(sleepDuration);
                } catch (InterruptedException ex) {
                    Logger.getLogger(IntelServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
                continue;
            }
        }







        resp.getWriter().println("got all 10 tasks back, hurray! \n\n");
        //Properties p = System.getProperties();
        //p.list(resp.getWriter());


    }
}