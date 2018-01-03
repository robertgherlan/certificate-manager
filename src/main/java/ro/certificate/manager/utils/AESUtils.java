package ro.certificate.manager.utils;

import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AESUtils {

	@Autowired
	private ConfigurationUtils configurationUtils;

	private static final String ALGORITHM = "AES";

	public String encryptKeyStorePassword(String data) throws Exception {
		return encrypt(data, configurationUtils.getKeyStoreEncryptionKey());
	}

	public String decryptKeyStorePassword(String encryptedData) throws Exception {
		return decrypt(encryptedData, configurationUtils.getKeyStoreEncryptionKey());
	}

	public String encryptPrivateKeyPassword(String data) throws Exception {
		return encrypt(data, configurationUtils.getPrivateKeyEncryptionKey());
	}

	public String decryptPrivateKeyPassword(String encryptedData) throws Exception {
		return decrypt(encryptedData, configurationUtils.getPrivateKeyEncryptionKey());
	}

	public String encryptCertificateID(String data) throws Exception {
		return encrypt(data, configurationUtils.getCertificateIDEncryptionKey());
	}

	public String decryptCertificateID(String encryptedData) throws Exception {
		return decrypt(encryptedData, configurationUtils.getCertificateIDEncryptionKey());
	}

	private String encrypt(String data, String encriptionKey) throws Exception {
		if (data != null && encriptionKey != null) {
			Key key = generateKey(encriptionKey);
			Cipher c = Cipher.getInstance(ALGORITHM);
			c.init(Cipher.ENCRYPT_MODE, key);
			byte[] encVal = c.doFinal(data.getBytes());
			return Base64.getEncoder().encodeToString(encVal);
		}

		return null;
	}

	private String decrypt(String encryptedData, String encryptionKey) throws Exception {
		if (encryptedData != null && encryptionKey != null) {
			Key key = generateKey(encryptionKey);
			Cipher c = Cipher.getInstance(ALGORITHM);
			c.init(Cipher.DECRYPT_MODE, key);
			byte[] decordedValue = Base64.getDecoder().decode(encryptedData);
			byte[] decValue = c.doFinal(decordedValue);
			return new String(decValue);
		}

		return null;
	}

	private Key generateKey(String encriptionKey) throws Exception {
		Key key = new SecretKeySpec(encriptionKey.getBytes(), ALGORITHM);
		return key;
	}
}
