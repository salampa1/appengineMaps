package com.fel.appenginemaps.intel;

import com.fel.bond.grids.TimeGrid;
import com.fel.bond.utility.Serializer;
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
public class QueueHandler {
    
    TimeGrid timegrid;
    public static final int SMUGGLERS_PER_TASK = 5;
    
    public TimeGrid provideIntel(int taskCount, HttpServletResponse resp) throws IOException {
        Queue queue;

        if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
            // if we are in app server, use pushqueue that targets backend module
            queue = QueueFactory.getQueue("pushqueue");
        } else {
            // if we are in local, use default queue that targets this module
            queue = QueueFactory.getDefaultQueue();
        }

        TaskOptions to = TaskOptions.Builder.withParam("smugglers", Integer.toString(SMUGGLERS_PER_TASK))
                .url("/backend").method(TaskOptions.Method.POST);

        

        //queue.add(to);
        for (int i = 0; i < taskCount; i++) {
            queue.add(to);
        }
       
        waitForResponses(taskCount, resp);
        
        
        return timegrid;
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
            TimeGrid taskOutput = null;
            try {
                taskOutput = (TimeGrid) Serializer.deserialize(payload);
            } catch (IOException ex) {
                Logger.getLogger(QueueHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(QueueHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if (timegrid==null && taskOutput != null) {
                timegrid = taskOutput;
            } else if (taskOutput != null) {
                joinTimeGrid(taskOutput);
            }
        }
    }
    
    private void joinTimeGrid(TimeGrid join) {
        for (int t = 0; t < timegrid.getTimeStepsNum(); t++) {
            for (int row = 0; row < timegrid.getRowSize(); row++) {
                for (int col = 0; col < timegrid.getColSize(); col++) {
                    double newValue = timegrid.getValue(t, row, col) + join.getValue(t, row, col);
                    timegrid.setValue(t, row, col, newValue);
                }
            }
        }
    }
}
