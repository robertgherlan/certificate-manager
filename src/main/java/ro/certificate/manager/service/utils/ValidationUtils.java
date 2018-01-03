package ro.certificate.manager.service.utils;

public class ValidationUtils {

	public static boolean validateUUID(String uuid) {
		if (uuid != null && uuid.length() == 36) {
			return true;
		}

		return false;
	}
}
