package ro.certificate.manager.service.utils;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ro.certificate.manager.entity.Document;
import ro.certificate.manager.entity.Keystore;
import ro.certificate.manager.entity.User;
import ro.certificate.manager.service.KeystoreService;
import ro.certificate.manager.utils.*;
import ro.certificate.manager.wrapper.Certificate;
import ro.certificate.manager.wrapper.CertificateDetails;

import javax.crypto.interfaces.DHKey;
import javax.crypto.spec.DHParameterSpec;
import javax.net.ssl.HttpsURLConnection;
import javax.security.auth.x500.X500Principal;
import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class CertificateUtils {

    private static final String KEYSTORE_TYPE = "JKS";

    private static final String RSA_ALGORITHM_NAME = "RSA";

    private static final String ELLIPTIC_CURVES_ALGORITHM_NAME = "EC";

    @Autowired
    private FolderUtils folderUtils;

    @Autowired
    private ConfigurationUtils configurationUtils;

    @Autowired
    private AESUtils aesUtils;

    @Autowired
    private KeystoreService keystoreService;

    @Autowired
    private SignatureUtils signatureUtils;


    public void generateCertificate(Certificate certificate, User user) throws Exception {
        String commonName = certificate.getCommonName();
        String keyStorePassword = StringGeneratorUtils.getRandomString();
        char[] keyStorePasswordChars = keyStorePassword.toCharArray();

        // Create an empty keyStore on disk.
        File keyStoreFile = createKeyStore(user.getId(), commonName, true);
        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(keyStoreFile))) {
            KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
            // Generate new key pair.
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(certificate.getEncryptionAlgorithm());
            if (Boolean.parseBoolean(configurationUtils.getUseSecureRandom())) {
                keyPairGenerator.initialize(certificate.getKeySize(), SecureRandomUtils.getSecureRandom());
            } else {
                keyPairGenerator.initialize(certificate.getKeySize());
            }

            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // Create subject based on provided certificate parameters such as: OU, O, E etc.
            X500Name name = getX500Name(certificate);

            Date currentDate = new Date();

            Date notAfter = calculateNotAfter(certificate.getValidityNumber(), certificate.getValidityType());

            BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());

            // Create new certificate.
            X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(name, serial, currentDate, notAfter, name, keyPair.getPublic());
            ContentSigner sigGen = new JcaContentSignerBuilder(certificate.getSignatureAlgorithm()).build(keyPair.getPrivate());
            X509Certificate cert = new JcaX509CertificateConverter().getCertificate(certGen.build(sigGen));

            // Validate created certificate.
            cert.checkValidity();
            cert.verify(cert.getPublicKey());
            keyStore.load(null);
            String privateKeyPassword = StringGeneratorUtils.getRandomString();
            char[] privateKeyPasswordChars = privateKeyPassword.toCharArray();
            // Save generated key pair and certificate in created keyStore.
            keyStore.setKeyEntry(commonName, keyPair.getPrivate(), privateKeyPasswordChars, new java.security.cert.Certificate[]{cert});


            Keystore keystore = new Keystore();
            keystore.setCertificateSubject(commonName);
            keystore.setKeyStorePassword(aesUtils.encryptKeyStorePassword(keyStorePassword));
            keystore.setPrivateKeyPassword(aesUtils.encryptPrivateKeyPassword(privateKeyPassword));
            keystore.setName(keyStoreFile.getName());
            keystore.setUser(user);
            keystore.setCreationDate(new Date());
            // Save created keyStore in database.
            keystoreService.save(keystore);

            // Save on disk created keyStore.
            keyStore.store(outputStream, keyStorePasswordChars);
        }
    }


    public void importCertificate(User user, String certificatePEM, String privateKeyPEM, byte[] certificateDERStream, byte[] privateKeyDERStream) throws Exception {
        OutputStream outputStream = null;
        try {
            java.security.cert.Certificate[] chain = extractCertificates(certificatePEM, certificateDERStream);
            String commonName = getCommonName((X509Certificate) chain[0]);
            PrivateKey privateKey = extractPrivateKey(privateKeyPEM, privateKeyDERStream);
            KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
            keyStore.load(null);
            String keyStorePassword = StringGeneratorUtils.getRandomString();
            String privateKeyPassword = StringGeneratorUtils.getRandomString();
            char[] privateKeyPasswordChars = privateKeyPassword.toCharArray();

            keyStore.setKeyEntry(commonName, privateKey, privateKeyPasswordChars, chain);
            File keyStoreFile = createKeyStore(user.getId(), commonName, true);
            outputStream = new BufferedOutputStream(new FileOutputStream(keyStoreFile));

            Keystore keystore = new Keystore();
            keystore.setCertificateSubject(commonName);
            keystore.setKeyStorePassword(aesUtils.encryptKeyStorePassword(keyStorePassword));
            keystore.setPrivateKeyPassword(aesUtils.encryptPrivateKeyPassword(privateKeyPassword));
            keystore.setName(keyStoreFile.getName());
            keystore.setUser(user);
            keystore.setCreationDate(new Date());
            keystoreService.save(keystore);
            char[] passwordChars = keyStorePassword.toCharArray();
            keyStore.store(outputStream, passwordChars);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                // Silent.
            }
        }
    }

    /**
     * Extract private key from PEM format or DER format.
     *
     * @param privateKeyPEM       Private key in PEM format.
     * @param privateKeyDERStream Private key in DER format.
     * @return extracted private key if is found, null otherwise.
     * @throws Exception If a error occurs.
     */
    private static PrivateKey extractPrivateKey(String privateKeyPEM, byte[] privateKeyDERStream) throws Exception {
        PEMParser parser = null;
        PrivateKey privateKey;
        try {
            if (privateKeyPEM != null) {
                parser = new PEMParser(new StringReader(privateKeyPEM));
                Object returnedObject = parser.readObject();
                privateKey = objectToPrivateKey(returnedObject);
            } else {
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyDERStream);
                KeyFactory keyFactory;
                try {
                    try {
                        keyFactory = KeyFactory.getInstance(RSA_ALGORITHM_NAME);
                        privateKey = keyFactory.generatePrivate(keySpec);
                    } catch (InvalidKeySpecException e) {
                        keyFactory = KeyFactory.getInstance(ELLIPTIC_CURVES_ALGORITHM_NAME);
                        privateKey = keyFactory.generatePrivate(keySpec);
                    }
                } catch (Exception e) {
                    parser = new PEMParser(new StringReader(new String(privateKeyDERStream)));
                    Object returnedObject = parser.readObject();
                    privateKey = objectToPrivateKey(returnedObject);
                }
            }
        } finally {
            try {
                if (parser != null) {
                    parser.close();
                }
            } catch (IOException e) {
                // Do nothing.
            }
        }

        if (privateKey == null) {
            throw new Exception(ErrorMessageBundle.PRIVATE_KEY_NOT_FOUND);
        }

        return privateKey;
    }


    private static PrivateKey objectToPrivateKey(Object object) throws Exception {
        PrivateKey privateKey = null;
        if (object instanceof PrivateKeyInfo) {
            PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) object;
            byte[] encoded = privateKeyInfo.getEncoded();
            KeyFactory keyFactory;
            try {
                keyFactory = KeyFactory.getInstance(RSA_ALGORITHM_NAME);
                privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encoded));
            } catch (InvalidKeySpecException e) {
                keyFactory = KeyFactory.getInstance(ELLIPTIC_CURVES_ALGORITHM_NAME);
                privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encoded));
            }
        } else if (object instanceof PEMKeyPair) {
            PEMKeyPair pemKeyPair = (PEMKeyPair) object;
            privateKey = new JcaPEMKeyConverter().getPrivateKey(pemKeyPair.getPrivateKeyInfo());
        }


        return privateKey;
    }


    private static java.security.cert.Certificate[] extractCertificates(String certificatePEM, byte[] certificateDER) throws Exception {
        java.security.cert.Certificate[] chain;
        try (InputStream inputStream = getInputStream(certificatePEM, certificateDER)) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Collection<?> c = cf.generateCertificates(inputStream);
            int numberOfCertificates = c.size();
            if (numberOfCertificates == 0) {
                throw new Exception(ErrorMessageBundle.CERTIFICATE_NOT_FOUND);
            }
            chain = new java.security.cert.Certificate[numberOfCertificates];
            Iterator<?> i = c.iterator();
            int counter = 0;
            while (i.hasNext()) {
                X509Certificate cert509 = (X509Certificate) i.next();
                // Check if introduced certificate is valid. If is not then a exception is thrown and the message will be visible in interface.
                cert509.checkValidity();
                chain[counter] = cert509;
                counter++;
            }
            return chain;
        }
    }

    private static InputStream getInputStream(String certificatePEM, byte[] certificateDER) {
        if (certificatePEM != null) {
            return new ByteArrayInputStream(certificatePEM.getBytes());
        }

        return new ByteArrayInputStream(certificateDER);
    }


    private static String getCommonName(X509Certificate x509Certificate) throws Exception {
        String commonName;
        try {
            X500Name x500name = new JcaX509CertificateHolder(x509Certificate).getSubject();
            RDN cn = x500name.getRDNs(BCStyle.CN)[0];
            commonName = IETFUtils.valueToString(cn.getFirst().getValue());
        } catch (Exception e) {
            throw new Exception(ErrorMessageBundle.INVALID_CERTIFICATE);
        }

        return commonName;
    }


    private File createKeyStore(String userID, String keyStoreName, boolean create) throws Exception {
        keyStoreName = folderUtils.getFileNameForWindows(keyStoreName);
        keyStoreName = keyStoreName + "_" + System.currentTimeMillis() + "_" + StringGeneratorUtils.generateRandomFileName() + ".keystore";
        File keyStoresFolderFile = folderUtils.getKeystoresFolderFile(userID);
        File keyStoreFile = new File(keyStoresFolderFile, keyStoreName);
        if (create) {
            if (keyStoreFile.exists()) {
                throw new Exception("The certificate with " + keyStoreFile.getName() + " name already exist on disk.");
            }
        } else {
            if (!keyStoreFile.exists()) {
                throw new Exception("The certificate with " + keyStoreFile.getName() + " name not exist on disk.");
            }
        }

        return keyStoreFile;
    }

    /**
     * Calculate last date of validity for a certificate, basically expired date.
     *
     * @param certificateValidity     Validity number. Represents the number of days, months or years.
     * @param certificateValidityType Represent the type of validity and values can be: year, month or day.
     * @return expired date for a certificate.
     */
    private static Date calculateNotAfter(int certificateValidity, String certificateValidityType) {
        Calendar calendar = Calendar.getInstance();
        if (certificateValidityType.toUpperCase().equals("YEARS")) {
            calendar.add(Calendar.YEAR, certificateValidity);
        } else if (certificateValidityType.toUpperCase().equals("MONTHS")) {
            calendar.add(Calendar.MONTH, certificateValidity);
        } else if (certificateValidityType.toUpperCase().equals("DAYS")) {
            calendar.add(Calendar.DAY_OF_MONTH, certificateValidity);
        } else {
            // Default value is 1 year.
            calendar.add(Calendar.YEAR, 1);
        }

        return calendar.getTime();
    }

    /**
     * Get a X500Name based on certificate parameters such as CN, OU, O etc.
     *
     * @param certificate The certificate parameters such as OU, O etc.
     * @return a object formed by given parameters.
     */
    private static X500Name getX500Name(Certificate certificate) {
        X500NameBuilder nameBuilder = new X500NameBuilder(BCStyle.INSTANCE);
        // I don't made test if common name is null because this field is mandatory.
        nameBuilder.addRDN(BCStyle.CN, certificate.getCommonName());

        String organizationUnit = certificate.getOrganizationUnit();
        if (organizationUnit != null && !organizationUnit.trim().isEmpty()) {
            nameBuilder.addRDN(BCStyle.OU, organizationUnit);
        }

        String organization = certificate.getOrganization();
        if (organization != null && !organization.trim().isEmpty()) {
            nameBuilder.addRDN(BCStyle.O, organization);
        }

        String locality = certificate.getLocality();
        if (locality != null && !locality.trim().isEmpty()) {
            nameBuilder.addRDN(BCStyle.L, locality);
        }

        String state = certificate.getState();
        if (state != null && !state.trim().isEmpty()) {
            nameBuilder.addRDN(BCStyle.ST, state);
        }

        String country = certificate.getCountry();
        if (country != null && !country.trim().isEmpty()) {
            nameBuilder.addRDN(BCStyle.C, country);
        }

        String email = certificate.getEmail();
        if (email != null && !email.trim().isEmpty()) {
            nameBuilder.addRDN(BCStyle.E, email);
        }

        return nameBuilder.build();
    }


    public List<CertificateDetails> getCertificatesInfo(Keystore keystore, User user) throws Exception {
        File keyStoreFile = getKeyStoreFile(user.getId(), keystore.getName());
        String keyStorePassword = aesUtils.decryptKeyStorePassword(keystore.getKeyStorePassword());
        KeyStore keyStore = loadKeyStoreFromDisk(keyStoreFile.getAbsolutePath(), keyStorePassword);
        Enumeration<String> aliases = keyStore.aliases();
        if (aliases == null) {
            throw new Exception(ErrorMessageBundle.CERTIFICATE_NOT_FOUND);
        }

        List<CertificateDetails> certificatesDetails = new ArrayList<>();
        String privateKeyPassword = aesUtils.decryptPrivateKeyPassword(keystore.getPrivateKeyPassword());
        while (aliases.hasMoreElements()) {
            String certificateAlias = aliases.nextElement();
            java.security.cert.Certificate[] certificates = keyStore.getCertificateChain(certificateAlias);
            Key key = keyStore.getKey(certificateAlias, privateKeyPassword.toCharArray());
            for (java.security.cert.Certificate cert : certificates) {
                CertificateDetails certificateDetails = getCertificateInformation(cert, key, certificateAlias);
                if (certificateDetails != null) {
                    certificatesDetails.add(certificateDetails);
                }
            }
        }

        return certificatesDetails;
    }


    private static CertificateDetails getCertificateInformation(java.security.cert.Certificate certificate, Key key, String certificateAlias) throws Exception {
        if (certificate instanceof X509Certificate) {
            X509Certificate x509Certificate = (X509Certificate) certificate;
            CertificateDetails certificateDetails = new CertificateDetails();
            certificateDetails.setAlias(certificateAlias);
            certificateDetails.setFormat(x509Certificate.getType());
            certificateDetails.setSelfSigned(isSelfSigned(x509Certificate));
            certificateDetails.setSubject(getSubject(x509Certificate));
            certificateDetails.setCommonName(getCommonName(x509Certificate));
            certificateDetails.setIssuer(getIssuer(x509Certificate));
            certificateDetails.setSignatureAlgorithm(x509Certificate.getSigAlgName());
            if (key instanceof PrivateKey) {
                certificateDetails.setKeySize(getPrivateKeySize((PrivateKey) key));
            }

            try {
                certificateDetails.setPem(getPemValue(x509Certificate));
            } catch (Exception e) {
                // Do nothing.
            }

            Date notBefore = x509Certificate.getNotBefore();
            Date notAfter = x509Certificate.getNotAfter();
            DateFormat iso8601DateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            certificateDetails.setNotAfter(iso8601DateFormat.format(notAfter));
            certificateDetails.setNotBefore(iso8601DateFormat.format(notBefore));
            boolean expired = true;
            try {
                x509Certificate.checkValidity();
                expired = false;
            } catch (Exception e) {
                // Do nothing.
            }
            certificateDetails.setExpired(expired);

            return certificateDetails;

        }
        return null;
    }


    private static String getPemValue(Object object) throws IOException {
        StringWriter stringWriter = new StringWriter();
        if (object instanceof X509Certificate || object instanceof Key) {
            try (JcaPEMWriter jcaPemWriter = new JcaPEMWriter(stringWriter)) {
                jcaPemWriter.writeObject(object);
                jcaPemWriter.flush();
            }
        }

        return stringWriter.toString();
    }

    private static boolean isSelfSigned(X509Certificate x509Certificate) {
        Principal issuerDN = x509Certificate.getIssuerDN();
        Principal subjectDN = x509Certificate.getSubjectDN();

        return issuerDN.equals(subjectDN);
    }

    private static String getIssuer(X509Certificate x509Certificate) {
        String issuerDnString = null;
        try {
            JcaX509CertificateHolder certHolder = new JcaX509CertificateHolder(x509Certificate);
            issuerDnString = certHolder.getIssuer().toString();
        } catch (CertificateEncodingException e) {
            // Silent.
        }

        if (issuerDnString == null) {
            Map<String, String> additionalOIDs = new HashMap<>();
            additionalOIDs.put(BCStyle.E.getId(), "E");
            issuerDnString = x509Certificate.getIssuerX500Principal().getName(X500Principal.RFC2253, additionalOIDs);
        }

        return issuerDnString;
    }

    private static String getSubject(X509Certificate x509Certificate) {
        String subjectDnString = null;
        try {
            JcaX509CertificateHolder certHolder = new JcaX509CertificateHolder(x509Certificate);
            subjectDnString = certHolder.getSubject().toString();
        } catch (CertificateEncodingException e) {
            // Silent.
        }

        if (subjectDnString == null) {
            Map<String, String> additionalOIDs = new HashMap<>();
            additionalOIDs.put(BCStyle.E.getId(), "E");
            subjectDnString = x509Certificate.getSubjectX500Principal().getName(X500Principal.RFC2253, additionalOIDs);
        }

        return subjectDnString;
    }

    private static KeyStore loadKeyStoreFromDisk(String keyStoreFullPath, String keyStorePassword) throws Exception {
        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(keyStoreFullPath))) {
            return loadKeyStore(inputStream, keyStorePassword);
        }
    }

    private static KeyStore loadKeyStore(InputStream inputStream, String keyStorePassword) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
        keyStore.load(inputStream, keyStorePassword.toCharArray());

        return keyStore;
    }

    private static int getPrivateKeySize(final PrivateKey key) {
        int size = 0;
        if (key instanceof RSAKey) {
            BigInteger modulus = ((RSAKey) key).getModulus();
            size = modulus.bitLength();
        } else if (key instanceof DSAKey) {
            DSAParams parameters = ((DSAKey) key).getParams();
            size = parameters.getP().bitLength();
        } else if (key instanceof DHKey) {
            DHParameterSpec parameters = ((DHKey) key).getParams();
            size = parameters.getP().bitLength();
        } else if (key instanceof ECKey) {
            ECPrivateKey ec = (ECPrivateKey) key;
            size = ec.getS().bitLength();
        }

        return size;
    }

    private static InputStream generateCSR(java.security.cert.Certificate certificate, Key key) throws Exception {
        StringWriter stringWriter = new StringWriter();
        try (JcaPEMWriter jcaPEMWriter = new JcaPEMWriter(stringWriter)) {
            if (certificate instanceof X509Certificate) {
                X509Certificate cert = (X509Certificate) certificate;
                PublicKey publicKey = cert.getPublicKey();
                JcaX509CertificateHolder certificateHolder = new JcaX509CertificateHolder(cert);
                X500Name subjectX500Name = certificateHolder.getSubject();
                if (subjectX500Name == null) {
                    throw new CertificateEncodingException(ErrorMessageBundle.CERTIFICATE_SUBJECT_NOT_FOUND);
                }

                ContentSigner contentSigner = new JcaContentSignerBuilder(cert.getSigAlgName()).build((PrivateKey) key);
                PKCS10CertificationRequestBuilder builder = new JcaPKCS10CertificationRequestBuilder(subjectX500Name, publicKey);
                PKCS10CertificationRequest csr = builder.build(contentSigner);

                jcaPEMWriter.writeObject(csr);
                jcaPEMWriter.flush();

                return new ByteArrayInputStream(stringWriter.toString().getBytes());
            } else {
                throw new Exception(ErrorMessageBundle.UNSUPPORTED_CERTIFICATE_FORMAT);
            }
        }
    }

    public InputStream generateCSR(Keystore keystore, User user) throws Exception {
        File keyStoreFile = getKeyStoreFile(user.getId(), keystore.getName());
        String keyStorePassword = aesUtils.decryptKeyStorePassword(keystore.getKeyStorePassword());
        KeyStore keyStore = loadKeyStoreFromDisk(keyStoreFile.getAbsolutePath(), keyStorePassword);
        Enumeration<String> aliases = keyStore.aliases();
        if (aliases == null) {
            throw new Exception(ErrorMessageBundle.CERTIFICATE_NOT_FOUND);
        }
        String certificateAlias = aliases.nextElement();
        if (certificateAlias == null) {
            throw new Exception(ErrorMessageBundle.CERTIFICATE_NOT_FOUND);
        }
        java.security.cert.Certificate certificate = keyStore.getCertificate(certificateAlias);
        String privateKeyPassword = aesUtils.decryptPrivateKeyPassword(keystore.getPrivateKeyPassword());
        Key key = keyStore.getKey(certificateAlias, privateKeyPassword.toCharArray());

        if (certificate == null || key == null) {
            throw new NotFoundException(ErrorMessageBundle.CERTIFICATE_NOT_FOUND);
        }
        return generateCSR(certificate, key);
    }


    public File getKeyStoreFile(String userId, String keyStoreFileName) throws Exception {
        File userKeyStoreDirectory = folderUtils.getKeystoresFolderFile(userId);
        File keyStoreFile = new File(userKeyStoreDirectory, keyStoreFileName);
        if (!keyStoreFile.exists()) {
            throw new Exception(ErrorMessageBundle.CERTIFICATE_NOT_FOUND);
        }

        return keyStoreFile;
    }

    public InputStream exportCertificate(Keystore keystore, User user) throws Exception {
        File keyStoreFile = getKeyStoreFile(user.getId(), keystore.getName());
        String keyStorePassword = aesUtils.decryptKeyStorePassword(keystore.getKeyStorePassword());
        KeyStore keyStore = loadKeyStoreFromDisk(keyStoreFile.getAbsolutePath(), keyStorePassword);

        Enumeration<String> aliases = keyStore.aliases();
        if (aliases == null) {
            throw new Exception(ErrorMessageBundle.CERTIFICATE_NOT_FOUND);
        }
        String certificateAlias = aliases.nextElement();
        if (certificateAlias == null) {
            throw new Exception(ErrorMessageBundle.CERTIFICATE_NOT_FOUND);
        }

        java.security.cert.Certificate certificate = keyStore.getCertificate(certificateAlias);
        return new ByteArrayInputStream(getPemValue(certificate).getBytes());
    }

    public InputStream exportPrivateKey(Keystore foundedKeystore, User user, Boolean asPEM) throws Exception {
        Key key = getPrivateKey(foundedKeystore, user);
        if (asPEM != null && asPEM) {
            return new ByteArrayInputStream(getPemValue(key).getBytes());
        }

        return new ByteArrayInputStream(key.getEncoded());
    }

    public Key getPrivateKey(Keystore foundedKeystore, User user) throws Exception {
        String keyStorePassword = aesUtils.decryptKeyStorePassword(foundedKeystore.getKeyStorePassword());
        File keyStoreFile = getKeyStoreFile(user.getId(), foundedKeystore.getName());
        KeyStore keyStore = loadKeyStoreFromDisk(keyStoreFile.getAbsolutePath(), keyStorePassword);
        Enumeration<String> aliases = keyStore.aliases();
        if (aliases == null || !aliases.hasMoreElements()) {
            throw new Exception(ErrorMessageBundle.CERTIFICATE_NOT_FOUND);
        }
        String certificateAlias = aliases.nextElement();
        if (certificateAlias == null) {
            throw new Exception(ErrorMessageBundle.CERTIFICATE_NOT_FOUND);
        }
        String privateKeyPassword = aesUtils.decryptPrivateKeyPassword(foundedKeystore.getPrivateKeyPassword());

        return keyStore.getKey(certificateAlias, privateKeyPassword.toCharArray());
    }

    public void uploadCertificate(MultipartFile certificate, MultipartFile privateKey, User user) throws Exception {
        if (certificate.isEmpty() && privateKey.isEmpty()) {
            throw new Exception("Both files are required.");
        }

        importCertificate(user, null, null, certificate.getBytes(), privateKey.getBytes());
    }

    public void deleteCertificate(User user, String id) {

    }

    public static byte[] signDocument(byte[] documentToSign, PrivateKey privateKey, String algorithm) throws Exception {
        Signature signature = Signature.getInstance(algorithm);
        signature.initSign(privateKey);
        signature.update(documentToSign);

        return signature.sign();
    }

    public static boolean verifyDocumentSignature(byte[] signatureData, byte[] documentData, java.security.cert.Certificate certificate) throws Exception {
        Signature signature = Signature.getInstance(((X509Certificate) certificate).getSigAlgName());
        signature.initVerify(certificate.getPublicKey());
        signature.update(documentData);
        return signature.verify(signatureData);
    }

    public static List<CertificateDetails> retrieveCertificates(URL destinationURL) throws Exception {
        HttpsURLConnection conn = (HttpsURLConnection) destinationURL.openConnection();
        conn.connect();
        java.security.cert.Certificate[] certs = conn.getServerCertificates();
        List<CertificateDetails> certificatesDetails = new ArrayList<>();
        for (java.security.cert.Certificate cert : certs) {
            CertificateDetails certificateDetails = getCertificateInformation(cert, null, destinationURL.getHost());
            if (certificateDetails != null) {
                certificatesDetails.add(certificateDetails);
            }
        }

        return certificatesDetails;
    }

    public KeyStore getKeyStore(Keystore keystore, User user) throws Exception {
        String keyStorePassword = aesUtils.decryptKeyStorePassword(keystore.getKeyStorePassword());
        File keyStoreFile = getKeyStoreFile(user.getId(), keystore.getName());
        return loadKeyStoreFromDisk(keyStoreFile.getAbsolutePath(), keyStorePassword);
    }

    public java.security.cert.Certificate getCertificate(Keystore keystore, User user) throws Exception {
        KeyStore keyStore = getKeyStore(keystore, user);
        return extractCertificateFromKeystore(keyStore);
    }

    public static String getAlias(KeyStore keyStore) throws KeyStoreException {
        Enumeration<String> aliases = keyStore.aliases();
        if (aliases == null || !aliases.hasMoreElements()) {
            throw new NotFoundException(ErrorMessageBundle.CERTIFICATE_NOT_FOUND);
        }
        String certificateAlias = aliases.nextElement();
        if (certificateAlias == null) {
            throw new NotFoundException(ErrorMessageBundle.CERTIFICATE_NOT_FOUND);
        }

        return certificateAlias;
    }

    public static java.security.cert.Certificate extractCertificateFromKeystore(KeyStore keyStore) throws KeyStoreException {
        String alias = getAlias(keyStore);
        java.security.cert.Certificate certificate = keyStore.getCertificate(alias);
        if (certificate == null) {
            throw new NotFoundException(ErrorMessageBundle.CERTIFICATE_NOT_FOUND);
        }

        return certificate;
    }

    public User verifySignature(MultipartFile signedDocument, MultipartFile signature) throws Exception {
        String signatureContent = new String(signature.getBytes());
        byte[] extractedSignature = Base64.getDecoder().decode(signatureUtils.extractSignature(signatureContent));
        String encryptedCertificateID = signatureUtils.extractCertificateID(signatureContent);
        if (extractedSignature == null || encryptedCertificateID == null) {
            throw new Exception(ErrorMessageBundle.INVALID_SIGNATURE_FILE);
        }
        String decryptedCertificateID = aesUtils.decryptCertificateID(encryptedCertificateID);
        Keystore keystore = keystoreService.findById(decryptedCertificateID);
        User user = keystore.getUser();
        java.security.cert.Certificate certificate = getCertificate(keystore, user);
        if (!(certificate instanceof X509Certificate)) {
            throw new Exception(ErrorMessageBundle.CERTIFICATE_NOT_FOUND);
        }
        if (!verifyDocumentSignature(extractedSignature, signedDocument.getBytes(), certificate)) {
            throw new Exception(ErrorMessageBundle.INVALID_SIGNATURE);
        }

        return user;
    }

    public Key extractPrivateKey(KeyStore keyStore, String password) throws Exception {
        String keyStorePassword = aesUtils.decryptPrivateKeyPassword(password);
        String alias = getAlias(keyStore);
        return keyStore.getKey(alias, keyStorePassword.toCharArray());
    }

    public boolean verifySignatureByUser(MultipartFile signedDocument, User user) {
        boolean success = false;
        try {
            List<Document> documents = user.getDocuments();
            if (documents != null && !documents.isEmpty()) {
                for (Document document : documents) {
                    try {
                        File documentFile = folderUtils.getDocumentFile(user.getId(), document.getPath());
                        success = IOUtils.contentEquals(new BufferedInputStream(new FileInputStream(documentFile)), signedDocument.getInputStream());
                        if (success) {
                            break;
                        }
                    } catch (Exception e) {
                        // Do nothing.
                    }
                }
            }
        } catch (Exception e) {
            // Do nothing.
        }

        return success;
    }
}