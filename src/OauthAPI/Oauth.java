package OauthAPI;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

public class Oauth  {

	private String email;

	private String ac_token;
	private String provider;

	private String name;

	private String imgUrl;
	

	
    public enum PROVIDER{
    	Facebook
    }
	
	public Oauth(Oauth.PROVIDER Provider){
		this.provider = Provider.toString();
	}
	public Oauth(String redir, Oauth.PROVIDER Provider){
		Oauth.redir_url=redir;
		this.provider = Provider.toString();
	}
	public void setEmail(String email){
		this.email=email;
	}
	public String getEmail() {
		return email;
	}
	public String getAc_token() {
		return ac_token;
	}
	public String getName() {
		return name;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setAccesstoken(String access_token){
		this.ac_token=access_token;
	}
	public void setUserName(String name){
		this.name=name;
	}
	public void setimgUrl(String url){
		this.imgUrl =url;
	}
	
	public static String redir_url;
	public static boolean isTouch=false;
	final public static String returnURL="http://follow-we.appspot.com/";
	public static void connectOauthProvider(Oauth oauth,HttpServletResponse resp){	
		
		if(oauth.provider.equals(Oauth.PROVIDER.Facebook.toString())){
			try {				
				resp.sendRedirect(FacebookOauth.connectOauthProvider());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
			
	}
	public static Oauth parseCode(Oauth userinfo, String code){
		if(userinfo.provider.equals(Oauth.PROVIDER.Facebook.toString())){
			return FacebookOauth.parseCode(code);
		}
		return null;
	}
	
	public static boolean setService(Oauth userinfo,HttpServletRequest req){
		HttpSession session= req.getSession(true);
		if(userinfo.provider.equals(Oauth.PROVIDER.Facebook.toString())){
			FacebookOauth fbsetOauth = new FacebookOauth(req);
			fbsetOauth.setEmail(userinfo.getEmail());
			fbsetOauth.setImgURL(userinfo.getImgUrl());
			fbsetOauth.setaccess_token(userinfo.getAc_token());
			fbsetOauth.setuserName(userinfo.getName());
			session.setAttribute("Oauth", "Facebook");
		}else{
			return false;
		}
		Queue queue = QueueFactory.getQueue("checkuser");		    
    	queue.add(withUrl("/api/checkuser").method(Method.POST).param("email", userinfo.getEmail())
    			  .param("name", userinfo.getName()).param("img", userinfo.getImgUrl()).param("oauth", userinfo.provider.toString()));
		
		return true;
	}

}
