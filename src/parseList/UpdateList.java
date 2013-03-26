package parseList;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import OauthAPI.OauthUser;
import OauthAPI.OauthFactory;

@SuppressWarnings("serial")
public class UpdateList  extends HttpServlet{
	public void doGet(HttpServletRequest req, HttpServletResponse resp)	throws IOException {
		resp.setCharacterEncoding("utf-8");
		resp.setContentType("text/plain;charset=utf-8");
		OauthUser user=OauthFactory.getService(req);
		try {
			if(user ==null){
				throw new Exception();
			}
			HttpSession session = req.getSession(true);
			if(session.getAttribute("nextUrl")==null)
				throw new Exception();
			ListHandler l=ListHandler.connect(session.getAttribute("nextUrl").toString(),req);
			if(l==null)
				throw new Exception();
			resp.getWriter().print("{\"data\":"+ l.getList()+ ", \"next\":\"" + l.getNextURL()+"\"}");
		} catch (Exception e) {
			// TODO: handle exception
			resp.getWriter().print("{\"error\":\"nodata\"}");
		}
		
	}
}
