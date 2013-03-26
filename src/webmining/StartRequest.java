package webmining;

import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

import OauthAPI.OauthFactory;
import OauthAPI.OauthUser;

@SuppressWarnings("serial")
public class StartRequest  extends HttpServlet{
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setCharacterEncoding("utf-8");
		resp.setContentType("text/plain;charset=utf-8");
		
		
		OauthUser user = OauthFactory.getService(req);
		if(user==null){
			resp.getWriter().print("none");
			return;
		}
		String search_key = req.getParameter("query").toString();
		search_key =search_key.trim();
		
		String uid = new String( user.getUID());		
		String target = req.getParameter("target")!=null ? req.getParameter("target").toString() : "me";
		
		
			URL next = new URL("http://proxysearch1.appspot.com/fbapi/show?" + "uid=" + uid+ 
				"&actoken="+ user.getaccess_token()+"&query="+search_key +"&target="+target );			
			HTTPRequest request = new HTTPRequest(next, HTTPMethod.GET, FetchOptions.Builder.withDeadline(60));
			URLFetchService uservice = URLFetchServiceFactory.getURLFetchService();			
			try {
				HTTPResponse response = uservice.fetch(request);
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
