package fbvideo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.*;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query.FilterOperator;
@SuppressWarnings("serial")
public class getTime extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		Calendar newtime = Calendar.getInstance();
		resp.getWriter().println((newtime.getTimeInMillis()/(long)1000));
	}
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws IOException {
		resp.setContentType("text/plain");
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		//if("1".equals(req.getParameter("type").toString())){
		StringTokenizer qStrings =new StringTokenizer(req.getParameter("id").toString(), "aa");
		//String id=req.getParameter("id").toString();
		String nameString=req.getParameter("email").toString();	
		
		StringBuffer list=new StringBuffer("");
		Query q= new Query("hasRead");
		List<String> qList=new ArrayList<String>();
		while(qStrings.hasMoreTokens())
			qList.add(qStrings.nextToken().trim());
		q.addFilter("linkid", FilterOperator.IN, qList);
		q.addFilter("email", FilterOperator.EQUAL, nameString);
		PreparedQuery pg=datastore.prepare(q);
		for (Entity result: pg.asIterable()){
			list.append(result.getProperty("linkid").toString()+",");		
		}		
		resp.getWriter().print(list.toString());
		//}
		//resp.getWriter().print(nameString);
	}
}
