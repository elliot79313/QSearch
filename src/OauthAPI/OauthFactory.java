package OauthAPI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class OauthFactory {
	
	public static OauthUser getService(HttpServletRequest req){
		HttpSession session= req.getSession(true);
		if(session.getAttribute("Oauth")==null){
			return null;
		}
		String provider=session.getAttribute("Oauth").toString();		
		if(provider.equals("Facebook"))
			return FacebookOauth.getService(req);
		
		return null;
	}
	
}
