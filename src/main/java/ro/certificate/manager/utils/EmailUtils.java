package ro.certificate.manager.utils;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ro.certificate.manager.mailer.Mail;
import ro.certificate.manager.service.utils.Mailer;

@Service
public class EmailUtils {

	@Autowired
	protected Mailer mailer;

	/**
	 * Send e email.
	 * 
	 * @param emailFrom
	 * @param emailTo
	 * @param subject
	 * @param templateName
	 * @param context
	 */
	public void sendEmail(String emailFrom, String emailTo, String subject, String templateName,
			HashMap<String, String> context) {
		Mail mail = new Mail();
		mail.setMailFrom(emailFrom);
		mail.setMailTo(emailTo);
		mail.setMailSubject(subject);
		mail.setTemplateName(templateName);
		mailer.sendMail(mail, context);
	}

}
