package OauthAPI;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class getFBcode extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)	throws IOException {
			resp.setCharacterEncoding("utf-8");
			resp.setContentType("text/plain;charset=utf-8");
			
			try {
				if(req.getParameter("code")!=null){
					Oauth userinfo =new Oauth( Oauth.PROVIDER.Facebook);
					String code=req.getParameter("code").toString();
					Oauth.setService(Oauth.parseCode(userinfo, code),req);					
					OauthUser user=OauthFactory.getService(req);
					if(user!=null){		
						/*
						resp.getWriter().println("Provider:"+ user.getProvider());
						resp.getWriter().println("user:"+ user.getuserName());
						resp.getWriter().println("Email:"+ user.getEmail());
						resp.getWriter().println("ImgUrl:"+ user.getImgURL());
						resp.getWriter().println("token:"+ user.getaccess_token());
						*/
						resp.sendRedirect("./");
					}else{
						resp.sendRedirect("./");
					}
				}else{
					resp.sendRedirect("./");
				}
			}catch (Exception e) {
					// TODO: handle exception
				e.printStackTrace();
			}
	}
}
