package ro.certificate.manager.utils;

import org.apache.commons.lang.RandomStringUtils;

public class StringGeneratorUtils {

    private StringGeneratorUtils() {
    }

    public static String getRandomString() {
        return RandomStringUtils.random(128, true, true);
    }

    public static String getUsernameString() {
        return RandomStringUtils.random(10, true, false);
    }

    public static String generateRandomFileName() {
        return RandomStringUtils.random(12, true, true);
    }
}
