package ro.certificate.manager.captcha;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import ro.certificate.manager.exceptions.ReCaptchaInvalidException;
import ro.certificate.manager.exceptions.ReCaptchaUnavailableException;
import ro.certificate.manager.utils.ConfigurationUtils;
import ro.certificate.manager.utils.RequestUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.regex.Pattern;

@Service("captchaService")
public class CaptchaService {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private RequestUtils requestUtils;

    @Autowired
    private ConfigurationUtils configurationUtils;

    @Autowired
    private AttemptsService attemptService;

    private RestOperations restTemplate = new RestTemplate();

    private static final Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");

    public void processResponse(final String response) {
        if (attemptService.isBlocked(requestUtils.getClientIP(request))) {
            throw new ReCaptchaInvalidException("Client exceeded maximum number of failed attempts");
        }

        if (!responseSanityCheck(response)) {
            throw new ReCaptchaInvalidException("Response contains invalid characters");
        }

        final URI verifyUri = URI.create(
                String.format("https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s&remoteip=%s", configurationUtils.getCaptchaSecretKey(), response, requestUtils.getClientIP(request)));
        try {
            final GoogleResponse googleResponse = restTemplate.getForObject(verifyUri, GoogleResponse.class);

            if (!googleResponse.isSuccess()) {
                if (googleResponse.hasClientError()) {
                    attemptService.reCaptchaFailed(requestUtils.getClientIP(request));
                }
                throw new ReCaptchaInvalidException("reCaptcha was not successfully validated");
            }
        } catch (RestClientException rce) {
            throw new ReCaptchaUnavailableException("Registration unavailable at this time.  Please try again later.", rce);
        }
        attemptService.reCaptchaSucceeded(requestUtils.getClientIP(request));
    }

    private boolean responseSanityCheck(final String response) {
        return StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches();
    }
}
