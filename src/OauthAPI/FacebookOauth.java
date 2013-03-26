package OauthAPI;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

class FacebookOauth implements OauthUser{
	
	@SuppressWarnings("unused")
	private String accestoken;
	@SuppressWarnings("unused")
	private boolean islogin;
	@SuppressWarnings("unused")
	private String Name;
	@SuppressWarnings("unused")
	private String email;
	private static String[] scope={"email", "publish_stream","read_stream"};
	private static String[] adscope={"offline_access"};
	@SuppressWarnings("unused")
	private String imgUrl;
	final private static String appId="127276800681016";
	final private static String secret ="";
	
	
	
	private HttpSession session;
	
	protected FacebookOauth(HttpServletRequest req){
		session = req.getSession(true);
		session.setMaxInactiveInterval(40*60);
	}
	@SuppressWarnings("deprecation")
	@Override
	public String getUID() {
		// TODO Auto-generated method stub
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query q= new Query("user");
		q.addFilter("email", FilterOperator.EQUAL, getEmail());
		PreparedQuery pq = datastore.prepare(q);		
		if(pq.countEntities()>0){
			for (Entity result : pq.asIterable()) {
				return Long.toString(result.getKey().getId());
			}
		}
		return null;
	}
	@Override
	public String getEmail() {
		// TODO Auto-generated method stub
		if(session.getAttribute("Email")==null)
			return null;
		return session.getAttribute("Email").toString();
	}
	@Override
	public String getaccess_token() {
		// TODO Auto-generated method stub
		if(session.getAttribute("ac_token")==null)
			return null;
		return session.getAttribute("ac_token").toString();
	}	
	@Override
	public String getuserName() {
		// TODO Auto-generated method stub
		if(session.getAttribute("Name")==null)
		return null;
		return session.getAttribute("Name").toString();
	}
	
	@Override
	public String getImgURL() {
		// TODO Auto-generated method stub
		if(session.getAttribute("imgUrl")==null)
			return null;
		return session.getAttribute("imgUrl").toString();
	}
	public String getProvider(){
	   return Oauth.PROVIDER.Facebook.toString();
	}
	
	public void setaccess_token(String ac_token) {
		// TODO Auto-generated method stub
		accestoken = ac_token;
		session.setAttribute("ac_token", ac_token);		
	}

	public void setEmail(String email) {
		// TODO Auto-generated method stub
		this.email=email;
		session.setAttribute("Email", email);
	}

	public void setuserName(String name) {
		// TODO Auto-generated method stub
		Name=name;
		session.setAttribute("Name", name);		
	}

	public void setImgURL(String url) {
		// TODO Auto-generated method stub
		imgUrl =url;
		session.setAttribute("imgUrl", url);
	}
	public Boolean getloginstatus() {
		// TODO Auto-generated method stub
		if(session.getAttribute("Oauth")==null)
			return false;
		return (session.getAttribute("Oauth").toString().equals("Facebook")?true:false);
	}
	
	public static String  connectOauthProvider(){
		//"&display=touch"
		String url="https://www.facebook.com/dialog/oauth?client_id="+FacebookOauth.appId +"&redirect_uri="+"http://fb-video.appspot.com/getfbcode"+(Oauth.isTouch==true?"&display=touch":"")+"&scope=";
		StringBuffer scopeurl= new StringBuffer("");		
		
			
		for (String temp: scope)
			scopeurl.append(","+temp);
		return url=url+scopeurl.toString().replaceFirst(",", "");   
		
		
	}
	public String advanceOauthProvider(){
		String url="https://www.facebook.com/dialog/oauth?client_id="+FacebookOauth.appId +"&redirect_uri="+"http://fb-video.appspot.com/getfbcode"+(Oauth.isTouch==true?"&display=touch":"")+"&scope=";
		StringBuffer scopeurl= new StringBuffer("");	
			
		for (String temp: scope)
			scopeurl.append(","+temp);
		for (String temp: adscope)
			scopeurl.append(","+temp);
		
		return url=url+scopeurl.toString().replaceFirst(",", "");
	}
	public static Oauth parseCode(String code){
		try {
			String url= "https://graph.facebook.com/oauth/access_token?client_id="+FacebookOauth.appId+ "&"+
				"redirect_uri=http://fb-video.appspot.com/getfbcode&"+
				"client_secret="+FacebookOauth.secret +"&"+
				"code="+code;	
			URLFetchService service = URLFetchServiceFactory.getURLFetchService();    
			HTTPResponse response = service.fetch(new HTTPRequest(new URL(url), HTTPMethod.GET));
			if(response.getResponseCode()==200){
				String url_getuser = "https://graph.facebook.com/me?fields=name,email&" + new String(response.getContent())
				+ "&format=json";
				HTTPResponse result = service.fetch(new URL(url_getuser));
				
				/*-- parse facebook response --*/				
				
    			JSONObject obj2 = new JSONObject(new String(result.getContent()));
    			
    			Oauth oauth = new Oauth(Oauth.PROVIDER.Facebook);    			
    			oauth.setAccesstoken(new String(response.getContent()));
    			oauth.setEmail(obj2.get("email").toString());
    			oauth.setUserName(obj2.get("name").toString());
    			oauth.setimgUrl("http://graph.facebook.com/"+ obj2.get("id").toString() +"/picture");	
    			
    			return oauth;
    			
			}
			return null;
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	@Override
	public void logout() {
		// TODO Auto-generated method stub
		
	}
	public void refresh_token(){
		
	}
	public static FacebookOauth getService(HttpServletRequest req){
		return new FacebookOauth(req);
	}

	
	
	
}
