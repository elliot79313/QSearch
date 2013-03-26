package parseList;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import OauthAPI.OauthFactory;
import OauthAPI.OauthUser;
@SuppressWarnings("serial")
public class Search extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)	throws IOException {
		resp.setCharacterEncoding("utf-8");
		resp.setContentType("text/plain;charset=utf-8");
		OauthUser user=OauthFactory.getService(req);
		try {
			if(user==null)
				throw new Exception();
			if(req.getParameter("code")==null)
				throw new Exception();
			resp.getWriter().print(ListHandler.search("me", user));//change me
		} catch (Exception e) {
			// TODO: handle exception
			resp.getWriter().print("{\"error\":\"nodata\"}");
		}
	}
}
