package parseList;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

import OauthAPI.OauthUser;

public class ListHandler {
	private String list;
	public String getList() {
		return list;
	}

	public void setList(String list) {
		this.list = list;
	}
	private String nextURL;
	public String getNextURL() {
		return nextURL;
	}

	public void setNextURL(String nextURL) {
		this.nextURL = nextURL;
	}

	public ListHandler(String list,String next) {
		// TODO Auto-generated constructor stub
		this.list=list;
		nextURL=next;
	}
	
	public static ListHandler connect(OauthUser user, HttpServletRequest req){
		String url="https://graph.facebook.com/me/home?limit=40&" + user.getaccess_token();
		ListHandler res = nextConnect(url);
		if(res!=null){
			HttpSession session = req.getSession(true);
			session.setAttribute("nextUrl", res.getNextURL());
		}
		return res;
	}
	public static ListHandler connect(String url, HttpServletRequest req){
		//String url="https://graph.facebook.com/me/home?limit=50&" + user.getaccess_token();
		ListHandler res = nextConnect(url);
		if(res!=null){
			HttpSession session = req.getSession(true);
			session.setAttribute("nextUrl", res.getNextURL());
		}
		return res;
	}
	public static ListHandler nextConnect(String url){
		URLFetchService service = URLFetchServiceFactory.getURLFetchService();
		try {
			HTTPResponse result = service.fetch(new URL(url));
			JSONObject obj = new JSONObject(new String(result.getContent()));
			JSONArray data = new JSONArray(obj.get("data").toString());
			JSONObject pageing = new JSONObject(obj.get("paging").toString());
			String nextpage = pageing.get("next").toString();
			
			JSONObject singleobj;
			JSONArray output = new JSONArray();			
			for (int i =0 ; i< data.length();i++){
				singleobj = new JSONObject(data.get(i).toString());
				if(singleobj.has("link")){
					if(singleobj.get("link").toString().indexOf("www.youtube.com")>=0){
						output.put(singleobj);
					}
				}
			}
			
			return new ListHandler(output.toString(),nextpage);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static JSONObject search(String id, OauthUser user){
		String url="https://graph.facebook.com/"+id+"/feed?limit=30&" + user.getaccess_token();
		URLFetchService service = URLFetchServiceFactory.getURLFetchService();
		try {
			HTTPResponse result = service.fetch(new URL(url));
			JSONObject obj = new JSONObject(new String(result.getContent()));
			JSONArray data = new JSONArray(obj.get("data").toString());
			JSONObject pageing = new JSONObject(obj.get("paging").toString());
			String nextpage = pageing.get("next").toString();
			
			JSONObject singleobj;
			JSONArray output = new JSONArray();			
			for (int i =0 ; i< data.length();i++){
				singleobj = new JSONObject(data.get(i).toString());
				if(singleobj.has("link")){
					if(singleobj.get("link").toString().indexOf("www.youtube.com")>=0){
						output.put(singleobj);
					}
				}
			}
			JSONObject out_obj = new JSONObject();
			out_obj.put("data", output);
			out_obj.put("next",  nextpage);
			return out_obj;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return null;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
