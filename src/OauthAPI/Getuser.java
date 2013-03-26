package OauthAPI;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

@SuppressWarnings("serial")
public class Getuser extends HttpServlet {
	@SuppressWarnings("deprecation")
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setCharacterEncoding("utf-8");
		resp.setContentType("text/plain;charset=utf-8");
		OauthUser user = OauthFactory.getService(req);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		String UID=null;
		if(req.getParameter("id")!=null){
			Key k= KeyFactory.createKey("user", Long.parseLong(UID));
			try {
				Entity e= datastore.get(k);
				resp.getWriter().print("{\"id\":\""+ e.getKey().getId() +"\",\"name\":\"" + 
										e.getProperty("name").toString()+ "\",\"img\":\"" + 
										e.getProperty("photo").toString()+"\"}");
				return;
				
			} catch (EntityNotFoundException e) {
				// TODO Auto-generated catch block
				resp.getWriter().print("{\"error\":\"Nofound\"}");
				return;
			}
				
							
		}else{
			if(user!=null){
				Query q= new Query("user");
				q.addFilter("email", FilterOperator.EQUAL, user.getEmail());
				PreparedQuery pq = datastore.prepare(q);
				if(pq.countEntities()>0)
					for (Entity result : pq.asIterable()) {
						UID= Long.toString(result.getKey().getId());
						break;
					}
				
				resp.getWriter().print("{\"id\":\""+ UID +"\",\"name\":\"" + 
									user.getuserName()+ "\",\"provider\":\"" +
									user.getProvider()+ "\",\"email\":\"" +
									user.getEmail()+ "\",\"img\":\"" +
									user.getImgURL()+"\"}");
				return;
			}
			resp.getWriter().print("{\"error\":\"Nofound\"}");
			return;
		}
	}
}
