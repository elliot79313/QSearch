
package fbvideo;
import java.io.IOException;
import javax.servlet.http.*;
import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailServiceFactory;
import com.google.appengine.api.mail.MailService.Message;

@SuppressWarnings("serial")
public class mailserver extends HttpServlet {

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		resp.setContentType("text/plain");
		HttpSession session= req.getSession(true);
		if (session.getAttribute("Email")!=null){
			if (session.getAttribute("Email").toString().length()>3){
				MailService mailsend=MailServiceFactory.getMailService();
				Message newmail=new Message();
				newmail.setSender("fb-video@fb-video.appspotmail.com");
				newmail.setTo("elliot79313@gmail.com");
				newmail.setSubject("Feedback");
				newmail.setHtmlBody("Hi from: "+session.getAttribute("Email").toString()+"<br/>"+req.getParameter("content").toString().replace("\n", "<br/>"));
				try {
					mailsend.send(newmail);
					resp.getWriter().println("send success");
				} catch (Exception e) {
					// TODO: handle exception
					resp.getWriter().println("send error");
				}
			}
		}else{
			resp.getWriter().println("No email from");
		}
		
	}
}
