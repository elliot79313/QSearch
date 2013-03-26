package fbvideo;

import java.io.IOException;
import java.net.URL;
import javax.servlet.http.*;
import com.google.appengine.api.urlfetch.*;
import org.json.simple.*;
import org.json.simple.parser.*;

@SuppressWarnings("serial")
public class fb_userinfo extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		String uid = new String(req.getParameter("uid").toString());
		String access = new String(req.getParameter("access").toString());
		URLFetchService url = URLFetchServiceFactory.getURLFetchService();
		String url1 = "https://api.facebook.com/method/users.getInfo?uids="
				+ uid + "&fields=email%2Cname&access_token=" + access
				+ "&format=json";
		HTTPResponse result = url.fetch(new URL(url1));
		JSONParser parser = new JSONParser();
		try {
			if (result.getResponseCode() == 200) {
				Object obj = parser.parse(new String(result.getContent()));
				JSONArray array = (JSONArray) obj;
				JSONObject obj2 = (JSONObject) array.get(0);
				resp.getWriter().println(obj2.get("email"));
			} else {
				
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block			
			e.printStackTrace();
		}

		resp.getWriter().close();
	}
}
