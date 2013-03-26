package fbvideo;
import java.io.IOException;
import javax.servlet.http.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
@SuppressWarnings("serial")
public class saveCluster extends HttpServlet{
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws IOException {
		resp.setContentType("text/plain");
		try {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		String[] qStrings =req.getParameter("id").toString().split(",");
		String nameString=req.getParameter("email").toString();
		
		if("1".equals(req.getParameter("type").toString())){
			List<Entity> qList=new ArrayList<Entity>();			
			Entity bob ;
		for( String Req : qStrings){
			bob=new Entity("hasRead",Req.trim()+"_"+nameString.hashCode());
			bob.setProperty("email", nameString);
			bob.setProperty("linkid", Req.trim());
			Calendar newtime= Calendar.getInstance();
			bob.setProperty("Time", newtime.getTimeInMillis()/(long)1000);
		qList.add(bob);
		}
		datastore.put(qList);
		resp.getWriter().print(req.getParameter("id").toString());
		}else{
			Query q= new Query("hasRead");
			List<String> qList=new ArrayList<String>();
			for( String Req : qStrings)
				qList.add(Req.trim());
			q.addFilter("linkid", FilterOperator.IN, qList);
			q.addFilter("email", FilterOperator.EQUAL, nameString);
			PreparedQuery pg=datastore.prepare(q);
			List<Key> kList=new ArrayList<Key>();			
			for (Entity result: pg.asIterable()){
				kList.add(result.getKey());	
			}
			datastore.delete(kList);
			resp.getWriter().print(req.getParameter("id").toString());
		}
		}catch(Exception e){
			
		}
		
	}
}
