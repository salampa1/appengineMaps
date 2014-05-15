package com.fel.appenginemaps.intel;

import com.fel.appenginemaps.servlets.IntelServlet;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TransientFailureException;
import com.google.appengine.api.utils.SystemProperty;
import com.google.apphosting.api.ApiProxy;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pavel Salamon <salampa1 at fel.cvut.cz>
 */
public class IntelProvider {
    
    double timegrid[][][];
    
    public String provideIntel(int taskCount) {
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

        waitForResponses(taskCount);
        
        
        return null;
    }
    
    
    private void waitForResponses(int taskCount) {
        Queue pull = QueueFactory.getQueue("pullqueue");
        
        int done = 0;

        while (done < taskCount) {
            int sleepDuration = 0;
            List<TaskHandle> leaseTasks = null;

            try {
                leaseTasks = pull.leaseTasks(1, TimeUnit.HOURS, 10); // lease 10 tasks
            } catch (TransientFailureException e) {
                sleepDuration = 500;
            } catch (ApiProxy.ApiDeadlineExceededException e) {
                sleepDuration = 500;
            }
            
            if (leaseTasks != null) {
                if (leaseTasks.isEmpty()) {
                    sleepDuration = 500;
                } else {
                    processResponses(leaseTasks);
                    done += leaseTasks.size();
                    pull.deleteTask(leaseTasks); // deleting done tasks
                }
            }
            
            if (sleepDuration > 0) {
                try {
                    Thread.sleep(sleepDuration);
                } catch (InterruptedException ex) {
                    Logger.getLogger(IntelServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
                continue;
            }
        }
        
        //pull.purge();
    }
    
    
    private void processResponses(List<TaskHandle> tasks) {
        
        for (TaskHandle t : tasks) {
            byte[] payload = t.getPayload();
            //timegrid[0][0][0] = 0; // ...
        }
    }
}
