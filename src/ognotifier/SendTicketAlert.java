package ognotifier;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendTicketAlert {
	public static void send() {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.sorint.it");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("user@sorint.it", "pass");
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("ogubchenko@sorint.it"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("ogubchenko@sorint.it"));
			message.setSubject("Hey Ho! New OrientDB ticket on PrjHub!");
			message.setText("Dear DevOps,"
					+ "\n\n New OrientDB ticket on PrjHub, go and check it! \n\n http://www.prjhub.com/#/issues?q=is:open%20client:%22_my%22&page=1 - PrjHub - My Clients Issues");

			Transport.send(message);

			System.out.println("Done");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}