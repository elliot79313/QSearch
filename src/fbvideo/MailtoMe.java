package fbvideo;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailServiceFactory;
import com.google.appengine.api.mail.MailService.Message;

@SuppressWarnings("serial")
public class MailtoMe extends HttpServlet{
	public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException{
		MailService mailsend=MailServiceFactory.getMailService();
		Message newmail=new Message();
		newmail.setSender("Shih-En <elliot79313@gmail.com>");
		newmail.setTo("Shih-En <elliot79313@gmail.com>");
		newmail.setSubject("[ASIAA][Summer School] Check");
		
		newmail.setHtmlBody("Dear Professor Yen:<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;My name is Shih-En Chou, one of prospective students. There are summer school projects announced at ASISS website. The projects about simulation are the most attractive to me. I wonder if I could make an appointment with you for understanding more details about the project ¡§Toward an Understanding of the Instability in the Circumnuclear Starburst Rings in the Barred Spiral Galaxies¡¨. I'd like to accomplish this project in this summer vacation.<br/><br/> Thanks.<br/><br/> --<br/> Best regard <br/>Shih-En Chou");
		try {
			mailsend.send(newmail);
			resp.getWriter().println("send success");
		} catch (Exception e) {
			// TODO: handle exception
			resp.getWriter().println("send error");
		}
	}
}
