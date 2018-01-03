package ro.certificate.manager.utils;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
public class StringGeneratorUtils {

	public String getRandomString() {
		return RandomStringUtils.random(128, true, true);
	}

	public String getUsernameString() {
		return RandomStringUtils.random(10, true, false);
	}

	public String getRandomNumberForPageTitle() {
		return RandomStringUtils.random(2, false, true);
	}

	public String getRandomNumberForFileName() {
		return RandomStringUtils.random(4, false, true);
	}

	public String generateRandomFileName() {
		return RandomStringUtils.random(12, true, true);
	}
}
