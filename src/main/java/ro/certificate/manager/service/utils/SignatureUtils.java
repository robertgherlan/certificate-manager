package ro.certificate.manager.service.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.certificate.manager.utils.AESUtils;

import java.io.*;
import java.util.Arrays;
import java.util.Base64;

@Component
public class SignatureUtils {

    @Autowired
    private FolderUtils folderUtils;

    @Autowired
    private AESUtils aesUtils;

    private static final String START_SIGNATURE = "-----BEGIN SIGNATURE-----\n";

    private static final String END_SIGNATURE = "\n-----END SIGNATURE-----\n";

    private static final String START_PRIVATE_KEY = "\n-----BEGIN PRIVATE KEY-----\n";

    private static final String END_PRIVATE_KEY = "\n-----END PRIVATE KEY-----";

    public String saveSignature(byte[] generatedSignature, String fileName, String userID, String keyStoreID) throws Exception {
        File signatureFolderFile = folderUtils.getSignaturesFolderFile(userID);
        String signatureFileName = folderUtils.generateFileName(fileName, true) + ".sig";
        File signatureFile = new File(signatureFolderFile, signatureFileName);
        FileUtils.checkIfExist(signatureFile);
        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(signatureFile))) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayOutputStream.write(START_SIGNATURE.getBytes());
            System.out.println("Sign: " + Arrays.toString(generatedSignature));
            byteArrayOutputStream.write(Base64.getEncoder().encode(generatedSignature));
            byteArrayOutputStream.write(END_SIGNATURE.getBytes());
            byteArrayOutputStream.write(START_PRIVATE_KEY.getBytes());
            keyStoreID = aesUtils.encryptCertificateID(keyStoreID);
            byteArrayOutputStream.write(keyStoreID.getBytes());
            byteArrayOutputStream.write(END_PRIVATE_KEY.getBytes());

            byteArrayOutputStream.writeTo(outputStream);
        }

        return signatureFileName;
    }

    public InputStream downloadSignature(String userId, String signatureFileName) throws Exception {
        File userSignatureFolder = folderUtils.getSignaturesFolderFile(userId);
        File signatureFile = new File(userSignatureFolder, signatureFileName);
        FileUtils.checkIfNotExist(signatureFile);

        return new BufferedInputStream(new FileInputStream(signatureFile));
    }

    public String extractSignature(String signature) {
        return StringUtils.substring(signature, START_SIGNATURE, END_SIGNATURE);
    }

    public String extractCertificateID(String signature) {
        return StringUtils.substring(signature, START_PRIVATE_KEY, END_PRIVATE_KEY);
    }
}
