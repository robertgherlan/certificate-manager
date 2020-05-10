package ro.certificate.manager.utils;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class RequestUtils {

	public String getClientIP(HttpServletRequest request) {
		final String xfHeader = request.getHeader("X-Forwarded-For");
		if (xfHeader == null) {
			return request.getRemoteAddr();
		}

		return xfHeader.split(",")[0];
	}

	public String getIPAddress(HttpServletRequest request) {
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
