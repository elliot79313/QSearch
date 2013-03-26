package fbvideo;
import java.io.IOException;
import javax.servlet.http.*;

import java.util.Calendar;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
@SuppressWarnings("serial")
public class readlog extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws IOException {
		resp.setContentType("text/plain");
		try {
			HttpSession session= req.getSession(true);
			String email=session.getAttribute("Email").toString();
			Entity bob = new Entity("hasRead",req.getParameter("name").toString()+"_"+email.hashCode());
			bob.setProperty("email", email);
			bob.setProperty("linkid", req.getParameter("name").toString());
			Calendar newtime= Calendar.getInstance();
			bob.setProperty("Time", newtime.getTimeInMillis()/(long)1000);
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			datastore.put(bob);
			resp.getWriter().println("success");
		} catch (Exception e) {
			resp.getWriter().println("error");
		}
		resp.getWriter().close();
	}
}
