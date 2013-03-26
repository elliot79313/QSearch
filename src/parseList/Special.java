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
import com.google.appengine.api.datastore.Query.SortDirection;

import fbvideo.CheckEqual;
@SuppressWarnings("serial")
public class Special extends HttpServlet{
	public void doGet(HttpServletRequest req, HttpServletResponse resp)	throws IOException {
		resp.setCharacterEncoding("utf-8");
		resp.setContentType("text/plain;charset=utf-8");
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		try {		
			Query q = new Query("Special");
			q.addSort("index", SortDirection.ASCENDING);
			PreparedQuery pg = datastore.prepare(q);
			JSONObject singleobj = null;
			JSONArray output_arr = new JSONArray();
			JSONObject output = new JSONObject();
			for (Entity result : pg.asIterable()) {
				singleobj= new JSONObject();
				singleobj.put("yid", result.getProperty("yid").toString());
				singleobj.put("name", result.getProperty("name").toString());
				output_arr.put(singleobj);
			}
		output.put("data", output_arr);
		resp.getWriter().print(output.toString());
		} catch (Exception e) {
			// TODO: handle exception
			resp.getWriter().print("{\"error\":\"nodata\"}");
		}
	}
	public void doPost(HttpServletRequest req, HttpServletResponse resp)	throws IOException {
		resp.setCharacterEncoding("utf-8");
		resp.setContentType("text/plain;charset=utf-8");
		try {
			String[] KEY = req.getParameterValues("KEY[]");
			String[] TITLE = req.getParameterValues("TITLE[]");
			if (!CheckEqual.equal(KEY.length, TITLE.length)) 
				throw new Exception();
			Query q = new Query("Special");
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			PreparedQuery pg = datastore.prepare(q);
			List<Key> kList = new ArrayList<Key>();
			for (Entity result : pg.asIterable()) {
				kList.add(result.getKey());
			}
			datastore.delete(kList);
			List<Entity> qList = new ArrayList<Entity>();
			Entity e;
			for (int x = 0; x < KEY.length; x++) {
				e = new Entity("Special");
				e.setProperty("yid", KEY[x]);
				e.setProperty("index", x);
				e.setProperty("name", TITLE[x]);
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
