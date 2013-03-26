package OauthAPI;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class AdvancePerm  extends HttpServlet{
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain;charset=UTF-8");
		OauthUser user = OauthFactory.getService(req);
		if(user!=null){
			resp.sendRedirect(user.advanceOauthProvider());
		}else{
			resp.sendRedirect("./login");
		}
	}
}
