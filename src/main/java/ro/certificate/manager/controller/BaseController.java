package ro.certificate.manager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import ro.certificate.manager.captcha.AttemptsService;
import ro.certificate.manager.captcha.CaptchaService;
import ro.certificate.manager.service.*;
import ro.certificate.manager.service.utils.CertificateUtils;
import ro.certificate.manager.utils.ConfigurationUtils;
import ro.certificate.manager.utils.EmailUtils;
import ro.certificate.manager.utils.ExtensionsUtils;
import ro.certificate.manager.utils.SecureRandomUtils;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import java.security.Security;
import java.util.Locale;

@Controller
public class BaseController {

    @Autowired
    protected ServletContext servletContext;

    @Autowired
    protected RoleService roleService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected DocumentService documentService;

    @Autowired
    protected SignatureService signatureService;

    @Autowired
    protected EmailUtils emailUtils;

    @Autowired
    protected CertificateUtils certificateGeneratorUtils;

    @Autowired
    protected KeystoreService keystoreService;

    @Autowired
    protected ExtensionsUtils extensionsUtils;

    @Autowired
    protected AttemptsService attemptsService;

    @Autowired
    protected CaptchaService captchaService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    protected ConfigurationUtils configurationUtils;

    protected final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12, SecureRandomUtils.getSecureRandom());

    public String getMessage(String key, Object[] args, Locale locale) {
        return messageSource.getMessage(key, args, locale);
    }

    public String getMessage(String key, Object args, Locale locale) {
        Object[] objects = {args};
        return messageSource.getMessage(key, objects, locale);
    }

    @PostConstruct
    private void init() {
        Security.setProperty("crypto.policy", "unlimited");
    }
}
