package ro.certificate.manager.service.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ro.certificate.manager.utils.AESUtils;

@Component
public class SignatureUtils {

	@Autowired
	private FolderUtils folderUtils;

	@Autowired
	private FileUtils fileUtils;

	@Autowired
	private StringUtils stringUtils;

	@Autowired
	private AESUtils aesUtils;

	private static final String START_SIGNATURE = "-----BEGIN SIGNATURE-----\n";

	private static final String END_SIGNATURE = "\n-----END SIGNATURE-----\n";

	private static final String START_PRIVATE_KEY = "\n-----BEGIN PRIVATE KEY-----\n";

	private static final String END_PRIVATE_KEY = "\n-----END PRIVATE KEY-----";

	public String saveSignature(byte[] generatedSignature, String fileName, String userID, String keyStoreID)
			throws Exception {
		File signatureFolderFile = folderUtils.getSignaturesFolderFile(userID);
		String signatureFileName = folderUtils.generateFileName(fileName, true) + ".sig";
		File signatureFile = new File(signatureFolderFile, signatureFileName);
		fileUtils.checkIfExist(signatureFile);
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			byteArrayOutputStream.write(START_SIGNATURE.getBytes());
			System.out.println("Sign: " + Arrays.toString(generatedSignature));
			byteArrayOutputStream.write(Base64.getEncoder().encode(generatedSignature));
			byteArrayOutputStream.write(END_SIGNATURE.getBytes());
			byteArrayOutputStream.write(START_PRIVATE_KEY.getBytes());
			keyStoreID = aesUtils.encryptCertificateID(keyStoreID);
			byteArrayOutputStream.write(keyStoreID.getBytes());
			byteArrayOutputStream.write(END_PRIVATE_KEY.getBytes());
			outputStream = new FileOutputStream(signatureFile);
			byteArrayOutputStream.writeTo(outputStream);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (Exception e) {
			}
			try {
				if (outputStream != null) {
					outputStream.close();
				}
			} catch (Exception e) {
			}
		}

		return signatureFileName;
	}

	public InputStream downloadSignature(String userId, String signatureFileName) throws Exception {
		File userSignatureFolder = folderUtils.getSignaturesFolderFile(userId);
		File signatureFile = new File(userSignatureFolder, signatureFileName);
		fileUtils.checkIfNotExist(signatureFile);

		return new FileInputStream(signatureFile);
	}

	public String extractSignature(String signature) {
		return stringUtils.substring(signature, START_SIGNATURE, END_SIGNATURE);
	}

	public String extractCertificateID(String signature) {
		return stringUtils.substring(signature, START_PRIVATE_KEY, END_PRIVATE_KEY);
	}
}
