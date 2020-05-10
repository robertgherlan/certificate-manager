package ro.certificate.manager.service.utils;

import lombok.extern.log4j.Log4j2;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import ro.certificate.manager.mailer.Mail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.StringWriter;
import java.util.HashMap;

@Log4j2
@Service
public class Mailer {

    @Autowired
    private JavaMailSenderImpl mailSender;

    @Autowired
    private VelocityEngine velocityEngine;

    public void setMailSender(JavaMailSenderImpl mailSender) {
        this.mailSender = mailSender;
    }

    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    public void sendMail(Mail mail, HashMap<String, String> velocityContextMap) {
        Template template = velocityEngine.getTemplate("./templates/" + mail.getTemplateName());
        VelocityContext velocityContext = new VelocityContext(velocityContextMap);
        StringWriter stringWriter = new StringWriter();
        template.merge(velocityContext, stringWriter);
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(mail.getMailTo());
            helper.setSubject(mail.getMailSubject());
            helper.setText(stringWriter.toString(), true);
        } catch (MessagingException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }

        mailSender.send(message);
    }
}