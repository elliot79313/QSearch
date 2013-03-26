package fbvideo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailServiceFactory;
import com.google.appengine.api.mail.MailService.Message;

@SuppressWarnings("serial")
public class admin_user extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException{
		resp.setContentType("text/plain");
		try{			
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			Query q= new Query("log");
			List<String> qList=new ArrayList<String>();
			qList.add("elliot79313@gmail.com");
			qList.add("elliot79313@hotmail.com");
		    q.addFilter("email", FilterOperator.IN, qList);
			
			PreparedQuery pg=datastore.prepare(q);
			List<Key> kList=new ArrayList<Key>();
			for (Entity result: pg.asIterable()){
				kList.add(result.getKey());
			}
			datastore.delete(kList);
			Calendar newtime = Calendar.getInstance();
			Query qtime= new Query("_ah_SESSION");
			qtime.addFilter("_expires", FilterOperator.LESS_THAN_OR_EQUAL, ((newtime.getTimeInMillis()/(long)1000)-3600*24)*1000);
			PreparedQuery pgtime=datastore.prepare(qtime);
			List<Key> kListtime=new ArrayList<Key>();
			for (Entity result: pgtime.asIterable()){
				kListtime.add(result.getKey());
			}
			datastore.delete(kListtime);
			
			MailService mailsend=MailServiceFactory.getMailService();
			Message newmail=new Message();
			newmail.setSender("fb-video@fb-video.appspotmail.com");
			newmail.setTo("elliot79313@gmail.com");
			newmail.setSubject("Cron job");
			newmail.setHtmlBody("Delete log " + kList.size() +" items.<br/>"+"Delete session " +kListtime.size()+" items.");
			try {
				mailsend.send(newmail);
				resp.getWriter().println("send success");
			} catch (Exception e) {
				// TODO: handle exception
				resp.getWriter().println("send error");
			}
		} catch(Exception e){
			resp.getWriter().println(e.toString());
		}
	}
}
