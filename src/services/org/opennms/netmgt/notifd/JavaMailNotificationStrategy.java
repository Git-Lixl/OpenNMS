/*
 * Created on Sep 8, 2004
 *
 * TODO Set Copyright
 */
package org.opennms.netmgt.notifd;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Category;
import org.opennms.core.utils.Argument;
import org.opennms.core.utils.ThreadCategory;
import org.opennms.netmgt.config.NotificationFactory;

import com.sun.mail.smtp.SMTPTransport;


/**
 * @author David Hustace
 *  
 */
public class JavaMailNotificationStrategy implements NotificationStrategy {

	/*
	 * These should be declared in the notificationCommands.xml file with
	 * Substitute/Switch statements.
	 */


	private static final String MAILER = "smtpsend";

	private static final String MAIL_HOST = "127.0.0.1";

	private static final boolean AUTHENTICATE = false;

	Session session = null;
	String _user = null;
	String _password = null;
	Message _msg = null;
	String _to = null;
	String _from = null;
	String _subject = null;
	String _messageText = null;
	
	Category log = null;

	/**
	 *  
	 */
	public JavaMailNotificationStrategy() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennms.netmgt.notifd.NotificationStrategy#send(java.util.List)
	 */
	public int send(List arguments) {
		
		log = ThreadCategory.getInstance(getClass());
				
		buildMessage(arguments);

		log.debug("In the JavaMailNotification class.");
		
		SMTPTransport t = null;
		try {
			t = (SMTPTransport) session.getTransport("smtp");
			if (AUTHENTICATE)
				t.connect(MAIL_HOST, _user, _password);
			else
				t.connect();
			t.sendMessage(_msg, _msg.getAllRecipients());
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} finally {
			System.out.println("Response: " + t.getLastServerResponse());
			try {
				t.close();
			} catch (MessagingException e1) {
				e1.printStackTrace();
			}
		}

		System.out.println("\nMail was sent successfully.");

		return 0;
	}

	/**
	 * @param arguments
	 */
	private void buildMessage(List arguments) {

		for (int i = 0; i < arguments.size(); i++) {

			Argument arg = (Argument) arguments.get(i);
			log.debug("Current arg switch: " + i + " of " + arguments.size() +" is: " + arg.getSwitch());
			log.debug("Current arg  value: " + i + " of " + arguments.size() +" is: " + arg.getValue());
			
			if (NotificationFactory.PARAM_EMAIL.equals(arg.getSwitch())) {
				log.debug("Found: PARAM_EMAIL");
				_to = arg.getValue();
			} else if (NotificationFactory.PARAM_SUBJECT.equals(arg.getSwitch())) {
				log.debug("Found: PARAM_SUBJECT");
				_subject = arg.getValue();
			} else if (NotificationFactory.PARAM_TEXT_MSG.equals(arg.getSwitch())) {
				log.debug("Found: PARAM_TEXT_MSG");
				_messageText = arg.getValue();
			}
		}

		Properties props = System.getProperties();

		if (MAIL_HOST != null)
			props.put("mail.smtp.host", MAIL_HOST);

		if (AUTHENTICATE)
			props.put("mail.smtp.auth", "true");

		// Get a Session object
		session = Session.getInstance(props, null);
		boolean debug = true;
		if (debug)
			session.setDebug(true);

		// construct the message
		_msg = new MimeMessage(session);

		try {
			_from = "root@127.0.0.1";
			if (_from != null)
				_msg.setFrom(new InternetAddress(_from));
			else
				_msg.setFrom();
			
			if (_to == null) {
				log.debug("_to is null");
				_to = "root@127.0.0.1";
			}
			log.debug("To is: "+ _to);
			_msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(_to, false));
			
			if (_subject == null) {
				log.debug("_subject is null");
				_subject = "Subject was null";
			}
			log.debug("Subject is: "+ _subject);
			_msg.setSubject(_subject);
			
			if (_messageText == null) {
				log.debug("_messageText is null");
				_messageText = "Message Text was null";
			}
			log.debug("Subject is: "+ _subject);
			_msg.setText(_messageText);
			
			_msg.setHeader("X-Mailer", MAILER);
			_msg.setSentDate(new Date());
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} finally {
			
		}
	}
}