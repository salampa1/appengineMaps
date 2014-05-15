package com.fel.appenginemaps.intel;

import com.fel.appenginemaps.servlets.IntelServlet;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TransientFailureException;
import com.google.appengine.api.utils.SystemProperty;
import com.google.apphosting.api.ApiProxy;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Pavel Salamon <salampa1 at fel.cvut.cz>
 */
public class IntelProvider {
    
    double timegrid[][][];
    
    public String provideIntel(int taskCount, HttpServletResponse resp) throws IOException {
        Queue queue;

        if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
            // if we are not app server, use pushqueue that targets backend module
            queue = QueueFactory.getQueue("pushqueue");
        } else {
            // if we are not local, use default queue that targets this module
            queue = QueueFactory.getDefaultQueue();
        }

        TaskOptions to = TaskOptions.Builder.withParam("smugglers", "10")
                .url("/backend").method(TaskOptions.Method.POST);

        

        //queue.add(to);
        for (int i = 0; i < taskCount; i++) {
            queue.add(to);
        }

        waitForResponses(taskCount, resp);
        
        
        return null;
    }
    
    
    private void waitForResponses(int taskCount, HttpServletResponse resp) throws IOException {
        Queue pull = QueueFactory.getQueue("pullqueue");
        
        int done = 0;

        while (done < taskCount) {
            long remainingMillis = ApiProxy.getCurrentEnvironment().getRemainingMillis();
            
            if (remainingMillis < 5000) {
                pull.purge();
                Queue push = QueueFactory.getQueue("pushqueue");
                push.purge();
                resp.getWriter().println("Only "+done+" tasks done.");
                return;
            }
            
            
            boolean sleep = false;
            List<TaskHandle> leaseTasks = null;

            try {
                leaseTasks = pull.leaseTasks(1, TimeUnit.HOURS, 10); // lease 10 tasks
            } catch (TransientFailureException e) {
                sleep = true;
            }
            
            if (leaseTasks != null) {
                if (leaseTasks.isEmpty()) {
                    sleep = true;
                } else {
                    processResponses(leaseTasks);
                    done += leaseTasks.size();
                    pull.deleteTask(leaseTasks); // deleting done tasks
                }
            }
            /*
            if (sleep) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(IntelServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
                continue;
            }*/
        }
        
    }
    
    
    private void processResponses(List<TaskHandle> tasks) {
        
        for (TaskHandle t : tasks) {
            byte[] payload = t.getPayload();
            //timegrid[0][0][0] = 0; // ...
        }
    }
}
