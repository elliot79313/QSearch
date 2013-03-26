package parseList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;

import fbvideo.CheckEqual;

import OauthAPI.OauthFactory;
import OauthAPI.OauthUser;

@SuppressWarnings("serial")
public class Favorite  extends HttpServlet{
	public void doGet(HttpServletRequest req, HttpServletResponse resp)	throws IOException {
		resp.setCharacterEncoding("utf-8");
		resp.setContentType("text/plain;charset=utf-8");
		OauthUser user=OauthFactory.getService(req);
		try {
			if(user==null)
				throw new Exception();
			Query q = new Query("Playlist");
			String email = user.getEmail();
			q.addFilter("email", FilterOperator.EQUAL, email);
			q.addSort("email", SortDirection.ASCENDING);
			q.addSort("index", SortDirection.ASCENDING);
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			PreparedQuery pg = datastore.prepare(q);
			JSONObject singleobj = null;
			JSONArray output = new JSONArray();	
			for (Entity result : pg.asIterable()) {
				singleobj= new JSONObject();
				singleobj.put("name", result.getProperty("titleStr").toString());				
				singleobj.put("yid", result.getProperty("youtubeId").toString());
				output.put(singleobj);
			}
			resp.getWriter().print("{\"data\":"+ output.toString()+ ", \"next\": null}");
		} catch (Exception e) {
			// TODO: handle exception
			resp.getWriter().print("{\"error\":\"nodata\"}");
		}
	}
	public void doPost(HttpServletRequest req, HttpServletResponse resp)	throws IOException {
		resp.setCharacterEncoding("utf-8");
		resp.setContentType("text/plain;charset=utf-8");
		OauthUser user=OauthFactory.getService(req);
		try {
			if(user==null)
				throw new Exception();
			String[] KEY = req.getParameterValues("KEY[]");
			String[] TITLE = req.getParameterValues("TITLE[]");			
			if (!CheckEqual.equal(KEY.length, TITLE.length)) 
				throw new Exception();
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			Query q = new Query("Playlist");
			q.addFilter("email", FilterOperator.EQUAL, user.getEmail());
			PreparedQuery pg = datastore.prepare(q);
			List<Key> kList = new ArrayList<Key>();
			for (Entity result : pg.asIterable()) {
				kList.add(result.getKey());
			}
			datastore.delete(kList);
			List<Entity> qList = new ArrayList<Entity>();
			Entity e;
			for (int x = 0; x < KEY.length; x++) {
				e = new Entity("Playlist", user.getEmail().trim() + "_" + x);
				e.setProperty("email", user.getEmail());
				e.setProperty("youtubeId", KEY[x]);
				e.setProperty("index", x);
				e.setProperty("titleStr", TITLE[x]);
				e.setProperty("ImageUrl", "http://i.ytimg.com/vi/" + KEY[x] +"/hqdefault.jpg");
				qList.add(e);
			}
			datastore.put(qList);
			resp.getWriter().print("{\"msg\":\"success\"}");
		} catch (Exception e) {
			// TODO: handle exception
			resp.getWriter().print("{\"error\":\"nodata\"}");
		}
	}
}
