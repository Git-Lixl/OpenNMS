/*
 * Created on Sep 13, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.opennms.core.utils;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Category;

import com.sun.mail.smtp.SMTPTransport;

	
/**
 * @author david
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class JavaMailer {

	private String _to;
	private String _from = "root@127.0.0.1";
	private String _subject;
	private String _messageText;
	private File file;
	private String MAIL_HOST;
	private String MAILER = "smtpsend";
	private boolean AUTHENTICATE = false;
	private String _user;
	private String _password;
	
	/**
	 * @return Returns the from.
	 */
	public String getFrom() {
		return _from;
	}
	/**
	 * @param from The from to set.
	 */
	public void setFrom(String from) {
		_from = from;
	}
	/**
	 * @return Returns the aUTHENTICATE.
	 */
	public boolean isAUTHENTICATE() {
		return AUTHENTICATE;
	}
	/**
	 * @param authenticate The aUTHENTICATE to set.
	 */
	public void setAUTHENTICATE(boolean authenticate) {
		AUTHENTICATE = authenticate;
	}
	/**
	 * @return Returns the file.
	 */
	public File getFile() {
		return file;
	}
	/**
	 * @param file The file to set.
	 */
	public void setFile(File file) {
		this.file = file;
	}
	/**
	 * @return Returns the mAIL_HOST.
	 */
	public String getMAIL_HOST() {
		return MAIL_HOST;
	}
	/**
	 * @param mail_host The mAIL_HOST to set.
	 */
	public void setMAIL_HOST(String mail_host) {
		MAIL_HOST = mail_host;
	}
	/**
	 * @return Returns the mAILER.
	 */
	public String getMAILER() {
		return MAILER;
	}
	/**
	 * @param mailer The mAILER to set.
	 */
	public void setMAILER(String mailer) {
		MAILER = mailer;
	}
	/**
	 * @return Returns the messageText.
	 */
	public String getMessageText() {
		return _messageText;
	}
	/**
	 * @param messageText The messageText to set.
	 */
	public void setMessageText(String messageText) {
		_messageText = messageText;
	}
	/**
	 * @return Returns the subject.
	 */
	public String getSubject() {
		return _subject;
	}
	/**
	 * @param subject The subject to set.
	 */
	public void setSubject(String subject) {
		_subject = subject;
	}
	/**
	 * @return Returns the to.
	 */
	public String getTo() {
		return _to;
	}
	/**
	 * @param to The to to set.
	 */
	public void setTo(String to) {
		_to = to;
	}
	
	public JavaMailer() {
		
	}

	/**
	 * @param text
	 * @param subject
	 * @param to
	 * 
	 */
	public void mailSend() {
		
		Category log = ThreadCategory.getInstance(getClass());
		
		Properties props = System.getProperties();

		if (MAIL_HOST != null)
			props.put("mail.smtp.host", MAIL_HOST);

		if (AUTHENTICATE)
			props.put("mail.smtp.auth", "true");

		// Get a Session object
		Session session = Session.getInstance(props, null);
		boolean debug = true;
		if (debug)
			session.setDebug(true);

		// construct the message
		Message _msg = new MimeMessage(session);

		try {
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

			
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} finally {
			
		}
	}
	
	/**
	 * @return Returns the password.
	 */
	public String getPassword() {
		return _password;
	}
	/**
	 * @param password The password to set.
	 */
	public void setPassword(String password) {
		_password = password;
	}
	/**
	 * @return Returns the user.
	 */
	public String getUser() {
		return _user;
	}
	/**
	 * @param user The user to set.
	 */
	public void setUser(String user) {
		_user = user;
	}
}