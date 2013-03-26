package webmining;

import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import OauthAPI.OauthFactory;
import OauthAPI.OauthUser;

import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

@SuppressWarnings("serial")
public class EndRequest extends HttpServlet{
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setCharacterEncoding("utf-8");
		resp.setContentType("text/plain;charset=utf-8");
		
		OauthUser user = OauthFactory.getService(req);
		if (user == null 
				|| req.getParameter("flag") == null) {
			resp.getWriter().print("null");
			return;
		}
		
		
		String uid = user.getUID();
		String flag = req.getParameter("flag").toString();
		String target = req.getParameter("target")!=null ? req.getParameter("target").toString() : "me";
		
		URL next = new URL("http://proxysearch1.appspot.com/fbapi/status?" + "uid=" + uid + 
				"&flag=" + flag+ "&target="+ target);			
			HTTPRequest request = new HTTPRequest(next, HTTPMethod.GET, FetchOptions.Builder.withDeadline(30));
			URLFetchService service = URLFetchServiceFactory.getURLFetchService();
			HTTPResponse response = service.fetch(request);
			try {
				if(response.getResponseCode()==200){					
					resp.getWriter().print(new String(response.getContent(), "UTF-8"));
				}
			} catch (Exception e) {
				// TODO: handle exception
				resp.getWriter().print("{\"error\":\""+ "error"+"\"}");
			}
			return;
	}
}
