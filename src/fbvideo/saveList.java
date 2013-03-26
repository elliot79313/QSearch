package fbvideo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;

@SuppressWarnings("serial")
public class saveList extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain;charset=UTF-8");
		try {
			HttpSession session = req.getSession(true);
			if (session.getAttribute("Email") != null) {
				String email = session.getAttribute("Email").toString();
				String[] KEY = req.getParameterValues("KEY[]");
				String[] TITLE = req.getParameterValues("TITLE[]");
				String[] IMG = req.getParameterValues("IMG[]");
				if (CheckEqual.equal(KEY.length, TITLE.length, IMG.length)) {
					try {
						DatastoreService datastore = DatastoreServiceFactory
								.getDatastoreService();
						// Delete old
						Query q = new Query("Playlist");
						q.addFilter("email", FilterOperator.EQUAL, email);
						PreparedQuery pg = datastore.prepare(q);
						List<Key> kList = new ArrayList<Key>();
						for (Entity result : pg.asIterable()) {
							kList.add(result.getKey());
						}
						datastore.delete(kList);
						// Add New
						List<Entity> qList = new ArrayList<Entity>();
						Entity bob;
						for (int x = 0; x < KEY.length; x++) {
							bob = new Entity("Playlist", email.trim() + "_" + x);
							bob.setProperty("email", email);
							bob.setProperty("youtubeId", KEY[x]);
							bob.setProperty("index", x);
							bob.setProperty("titleStr", TITLE[x]);
							bob.setProperty("ImageUrl", IMG[x]);
							qList.add(bob);
						}
						datastore.put(qList);
						
					} catch (Exception e) {
						
					}
				} 
			}
		} catch (Exception e) {
			HttpSession session = req.getSession(true);
			if (session.getAttribute("Email") != null) {
				String email = session.getAttribute("Email").toString();
				DatastoreService datastore = DatastoreServiceFactory
						.getDatastoreService();
				// Delete old
				Query q = new Query("Playlist");
				q.addFilter("email", FilterOperator.EQUAL, email);
				PreparedQuery pg = datastore.prepare(q);
				List<Key> kList = new ArrayList<Key>();
				for (Entity result : pg.asIterable()) {
					kList.add(result.getKey());
				}
				datastore.delete(kList);
			}
		}
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain;charset=UTF-8");
		try {
			HttpSession session = req.getSession(true);
			if (session.getAttribute("Email") != null) {
				DatastoreService datastore = DatastoreServiceFactory
						.getDatastoreService();
				Query q = new Query("Playlist");
				String email = session.getAttribute("Email").toString();
				q.addFilter("email", FilterOperator.EQUAL, email);
				q.addSort("email", SortDirection.ASCENDING);
				q.addSort("index", SortDirection.ASCENDING);
				PreparedQuery pg = datastore.prepare(q);

				StringBuffer outputBuffer = new StringBuffer("");
				for (Entity result : pg.asIterable()) {
					outputBuffer.append(",['" + result.getProperty("youtubeId")
							+ "','" + result.getProperty("titleStr") + "','"+result.getProperty("ImageUrl")+"']");
				}
				resp.getWriter().print(
						"[" + outputBuffer.toString().replaceFirst(",", "")
								+ "]");

			} else {
				resp.getWriter().print("O");
			}
		} catch (Exception e) {
			// TODO: handle exception
			resp.getWriter().print(e);
		}
	}
}
