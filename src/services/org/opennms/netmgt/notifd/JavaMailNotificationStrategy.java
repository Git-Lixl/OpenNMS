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
import org.opennms.core.utils.JavaMailer;

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
		log.debug("In the JavaMailNotification class.");
				
		JavaMailer jm = buildMessage(arguments);
		
		jm.mailSend();
		
		return 0;
	}

	/**
	 * This method extracts the to, subject, and message text from
	 * the parameters passed in the notification.
	 * @param arguments
	 */
	private JavaMailer buildMessage(List arguments) {

		JavaMailer jm = new JavaMailer();
				
		for (int i = 0; i < arguments.size(); i++) {

			Argument arg = (Argument) arguments.get(i);
			log.debug("Current arg switch: " + i + " of " + arguments.size() +" is: " + arg.getSwitch());
			log.debug("Current arg  value: " + i + " of " + arguments.size() +" is: " + arg.getValue());
			
			if (NotificationFactory.PARAM_EMAIL.equals(arg.getSwitch())) {
				log.debug("Found: PARAM_EMAIL");
				jm.setTo(arg.getValue());
			} else if (NotificationFactory.PARAM_SUBJECT.equals(arg.getSwitch())) {
				log.debug("Found: PARAM_SUBJECT");
				jm.setSubject(arg.getValue());
			} else if (NotificationFactory.PARAM_TEXT_MSG.equals(arg.getSwitch())) {
				log.debug("Found: PARAM_TEXT_MSG");
				jm.setMessageText(arg.getValue());
			}
		}
		
		return jm;
	}

}