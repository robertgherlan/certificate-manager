package ro.certificate.manager.service.utils;

import java.util.UUID;

public class ValidationUtils {

    public static boolean validateUUID(String uuid) {
        if (uuid != null && uuid.length() == 36) {
            try {
                UUID.fromString(uuid);
                return true;
            } catch (IllegalArgumentException e) {
                // Stay silent.
            }
        }

        return false;
    }
}
