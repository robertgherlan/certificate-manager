package ro.certificate.manager.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationUtils {

    private static final String keyStoreEncryptionKey = "q6hB54B7D6cj4bt4";

    private static final String privateKeyEncryptionKey = "Kpj5mBuABDBcR7Z5";

    private static final String certificateIDEncryptionKey = "ubwBjLO8qgdRwZh0";

    @Value("${userFiles.folder}")
    private String userFilesFolder;

    @Value("use.secure.random")
    private final String useSecureRandom = "false";

    @Value("${captcha.siteKey}")
    private String captchaSiteKey;

    @Value("${captcha.secretKey}")
    private String captchaSecretKey;

    @Value("${file.maxUploadSize}")
    private Long maxUploadSize;

    @Value("${email.username}")
    private String email;

    @Value("${site.homeURL}")
    private String siteHomeURL;

    @Value("${contact.email}")
    private String contactEmail;

    @Value("${email.title}")
    private String emailTitle;

    @Value("${attempts.maxAttempts}")
    private Integer maxAttempts;

    @Value("${attempts.interval}")
    private Integer attemptsInterval;

    public String getCaptchaSiteKey() {
        return captchaSiteKey;
    }

    public String getCaptchaSecretKey() {
        return captchaSecretKey;
    }

    public Long getMaxUploadSize() {
        return maxUploadSize;
    }

    public String getEmail() {
        return email;
    }

    public String getSiteHomeURL() {
        return siteHomeURL;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public String getEmailTitle() {
        return emailTitle;
    }

    public String getUserFilesFolder() {
        return userFilesFolder;
    }

    public String getUseSecureRandom() {
        return useSecureRandom;
    }

    public String getKeyStoreEncryptionKey() {
        return keyStoreEncryptionKey;
    }

    public Integer getMaxAttempts() {
        return maxAttempts;
    }

    public Integer getAttemptsInterval() {
        return attemptsInterval;
    }

    public String getPrivateKeyEncryptionKey() {
        return privateKeyEncryptionKey;
    }

    public String getCertificateIDEncryptionKey() {
        return certificateIDEncryptionKey;
    }

}
