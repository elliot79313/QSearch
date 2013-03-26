package OauthAPI;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class Fblogin  extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain;charset=UTF-8");
		OauthUser user = OauthFactory.getService(req);
		
		if(user==null){
			String redir=Oauth.returnURL;
			if (req.getParameter("url")!=null)
				if(!req.getParameter("url").toString().equals("./"))
					redir = req.getParameter("url").toString();
			if (req.getParameter("touch")!=null)
				Oauth.isTouch=true;
			Oauth auth =new Oauth(redir, Oauth.PROVIDER.Facebook);
			Oauth.connectOauthProvider(auth, resp);
		}else{
			/*
			resp.getWriter().println("Provider:"+ user.getProvider());
			resp.getWriter().println("user:"+ user.getuserName());
			resp.getWriter().println("Email:"+ user.getEmail());
			resp.getWriter().println("ImgUrl:"+ user.getImgURL());
			resp.getWriter().println("token:"+ user.getaccess_token());
			*/
			resp.sendRedirect("./");
		}
	}
}
