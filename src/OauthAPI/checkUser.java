package OauthAPI;

import java.io.IOException;
import java.util.Calendar;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

@SuppressWarnings("serial")
public class checkUser extends HttpServlet{
	@SuppressWarnings("deprecation")
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setCharacterEncoding("utf-8");
		resp.setContentType("text/plain;charset=utf-8");
		try {
			if(req.getParameter("name")==null || req.getParameter("img")==null){
				throw new Exception();
			}if(req.getParameter("email")==null || req.getParameter("oauth")==null){
				throw new Exception();
			}
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			Query q= new Query("user");
			q.addFilter("email", FilterOperator.EQUAL, req.getParameter("email").toString());
			PreparedQuery pq = datastore.prepare(q);
			
			if(pq.countEntities()>0){
				Entity e=null;
				for (Entity result : pq.asIterable()) {
					e=result;
					break;
				}
				if(e!=null){
					Calendar n_time = Calendar.getInstance();
					e.setProperty("name", req.getParameter("name").toString());
					e.setProperty("photo", req.getParameter("img").toString());					
					e.setProperty("Oauth", req.getParameter("oauth").toString());
					e.setProperty("status", "1");
					e.setProperty("last_time", n_time.getTimeInMillis() / (long) 1000);
					e.setProperty("uid", Long.toString(e.getKey().getId()));
					datastore.put(e);
					return;
				}
			}else{
				Entity e= new Entity("user");
				Calendar n_time = Calendar.getInstance();
				e.setProperty("name", req.getParameter("name").toString());
				e.setProperty("email", req.getParameter("email").toString());
				e.setProperty("photo", req.getParameter("img").toString());					
				e.setProperty("Oauth", req.getParameter("oauth").toString());
				e.setProperty("status", "1");
				e.setProperty("last_time", n_time.getTimeInMillis() / (long) 1000);
				
				
				Key k=datastore.put(e);
				Entity tmpE= datastore.get(k);
				tmpE.setProperty("uid", Long.toString(k.getId()));
				datastore.put(tmpE);
				return;
			}
				
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
