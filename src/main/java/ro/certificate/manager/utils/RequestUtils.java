package ro.certificate.manager.utils;

import javax.servlet.http.HttpServletRequest;

public class RequestUtils {

    private RequestUtils() {
    }

    public static String getClientIP(HttpServletRequest request) {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }

        return xfHeader.split(",")[0];
    }

    public static String getIPAddress(HttpServletRequest request) {
        String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader("X-Forwarded-For");
            if (remoteAddr == null || remoteAddr.trim().isEmpty()) {
                remoteAddr = request.getRemoteAddr();
                if (remoteAddr == null || remoteAddr.trim().isEmpty()) {
                    remoteAddr = "Unknown";
                }
            }
        }

        return remoteAddr;
    }
}
