package fbvideo;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import javax.servlet.http.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

@SuppressWarnings("serial")
public class Fb_videoServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain;charset=utf-8");
		resp.setCharacterEncoding("utf-8");
		resp.getWriter().println("Hello, world!!");
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain;charset=utf-8");
		resp.setCharacterEncoding("utf-8");
		HttpSession session = req.getSession(true);
		try {
			String uid = new String(req.getParameter("uid").toString());
			String access = new String(req.getParameter("access").toString());
			URLFetchService url = URLFetchServiceFactory.getURLFetchService();
			String url1 = "https://api.facebook.com/method/users.getInfo?uids="
					+ uid + "&fields=email%2Cname%2Csex&access_token=" + access
					+ "&format=json";
			HTTPResponse result = url.fetch(new URL(url1));
			JSONParser parser = new JSONParser();
			try {
				// parse data
				if (result.getResponseCode() == 200) {
					Object obj = parser.parse(new String(result.getContent()));
					JSONArray array = (JSONArray) obj;
					JSONObject obj2 = (JSONObject) array.get(0);
					// Store data
					Entity bob = new Entity("log");
					bob.setProperty("email", obj2.get("email"));
					bob.setProperty("name", obj2.get("name"));
					bob.setProperty("gender", obj2.get("sex"));
					Calendar newtime = Calendar.getInstance();
					bob.setProperty("Time", newtime.getTimeInMillis()
							/ (long) 1000);
					DatastoreService datastore = DatastoreServiceFactory
							.getDatastoreService();
					datastore.put(bob);

					session.setAttribute("Email", obj2.get("email"));
					resp.getWriter().println("Welcome~ " + obj2.get("name"));
				} else {
					session.invalidate();
					resp.getWriter().println("Welcome~ ");
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (Exception e) {
			resp.getWriter().println("error");
		}
		resp.getWriter().close();
	}
}
