package ro.certificate.manager.utils;

import java.security.SecureRandom;

public class SecureRandomUtils {

    private SecureRandomUtils() {
    }

    public static SecureRandom getSecureRandom() {
        SecureRandom secureRandom;
        try {
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
        } catch (Exception e) {
            secureRandom = new SecureRandom();
        }

        return secureRandom;
    }
}
