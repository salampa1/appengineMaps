package development;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.utils.SystemProperty;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Pavel Salamon <salampa1 at fel.cvut.cz>
 */
public class DevelopmentServlet  extends HttpServlet {

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

        //Properties p = System.getProperties();
        //p.list(resp.getWriter());

        Queue queue = QueueFactory.getQueue("pullqueue");


        TaskOptions to = TaskOptions.Builder.withMethod(TaskOptions.Method.PULL).payload("hello world");

        queue.add(to);
        resp.getWriter().println("Added to pullqueue! \n\n");




    }
}
