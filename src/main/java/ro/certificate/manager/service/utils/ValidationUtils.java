package ro.certificate.manager.service.utils;

public class ValidationUtils {

	public static boolean validateUUID(String uuid) {
        return uuid != null && uuid.length() == 36;
    }
}
