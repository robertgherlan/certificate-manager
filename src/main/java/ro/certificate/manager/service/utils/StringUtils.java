package ro.certificate.manager.service.utils;

import org.springframework.stereotype.Component;

@Component
public class StringUtils {

	public String substring(String content, String startMark, String endMark) {
		String substring = null;
		if (content != null && content.trim().length() > 0) {
			int start = content.indexOf(startMark);
			if (start != -1) {
				start = start + startMark.length();
				int end = content.indexOf(endMark);
				if (end > 0 && end > start) {
					substring = content.substring(start, end);
				}
			}
		}

		return substring;
	}
}
