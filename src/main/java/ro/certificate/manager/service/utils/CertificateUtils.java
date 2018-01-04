package ro.certificate.manager.service.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.URL;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAKey;
import java.security.interfaces.DSAParams;
import java.security.interfaces.ECKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.RSAKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.crypto.interfaces.DHKey;
import javax.crypto.spec.DHParameterSpec;
import javax.net.ssl.HttpsURLConnection;
import javax.security.auth.x500.X500Principal;

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
import ro.certificate.manager.utils.AESUtils;
import ro.certificate.manager.utils.ConfigurationUtils;
import ro.certificate.manager.utils.ErrorMessageBundle;
import ro.certificate.manager.utils.SecureRandomUtils;
import ro.certificate.manager.utils.StringGeneratorUtils;
import ro.certificate.manager.wrapper.Certificate;
import ro.certificate.manager.wrapper.CertificateDetails;

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

	/**
	 * Date format for ISO 8601.
	 */
	private DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	@Autowired
	private StringGeneratorUtils stringGeneratorUtils;

	@Autowired
	private KeystoreService keystoreService;

	@Autowired
	private SignatureUtils signatureUtils;

	/**
	 * Generate a certificate using details from interface.
	 * 
	 * @param certificate
	 * @param user
	 * @throws Exception
	 */
	public void generateCertificate(Certificate certificate, User user) throws Exception {
		String commonName = certificate.getCommonName();
		String keyStorePassword = stringGeneratorUtils.getRandomString();
		char[] keyStorePasswordChars = keyStorePassword.toCharArray();

		// Create an empty keyStore on disk.
		File keyStoreFile = createKeyStore(commonName, user.getId(), keyStorePassword, true);

		FileOutputStream fileOutputStream = null;

		try {
			KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
			// Generate new key pair.
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(certificate.getEncryptionAlgorithm());
			if (Boolean.valueOf(configurationUtils.getUseSecureRandom())) {
				keyPairGenerator.initialize(certificate.getKeySize(), SecureRandomUtils.getSecureRandom());
			} else {
				keyPairGenerator.initialize(certificate.getKeySize());
			}

			KeyPair keyPair = keyPairGenerator.generateKeyPair();

			// Create subject based on provided certificate parameters such as:
			// OU, O, E etc.
			X500Name name = getX500Name(certificate);

			Date currentDate = new Date();

			Date notAfter = calculateNotAfter(certificate.getValidityNumber(), certificate.getValidityType());

			BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());

			// Create new certificate.
			X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(name, serial, currentDate, notAfter,
					name, keyPair.getPublic());
			ContentSigner sigGen = new JcaContentSignerBuilder(certificate.getSignatureAlgorithm())
					.build(keyPair.getPrivate());
			X509Certificate cert = new JcaX509CertificateConverter().getCertificate(certGen.build(sigGen));

			// Validate created certificate.
			cert.checkValidity();
			cert.verify(cert.getPublicKey());
			keyStore.load(null);
			String privateKeyPassword = stringGeneratorUtils.getRandomString();
			char[] privateKeyPasswordChars = privateKeyPassword.toCharArray();
			// Save generated key pair and certificate in created keyStore.
			keyStore.setKeyEntry(commonName, keyPair.getPrivate(), privateKeyPasswordChars,
					new java.security.cert.Certificate[] { cert });

			fileOutputStream = new FileOutputStream(keyStoreFile);
			Keystore keystore = new Keystore();
			keystore.setCertificateSubject(commonName);
			keystore.setKeyStorePassword(aesUtils.encryptKeyStorePassword(keyStorePassword));
			keystore.setPrivateKeyPassword(aesUtils.encryptPrivateKeyPassword(privateKeyPassword));
			keystore.setName(keyStoreFile.getName());
			keystore.setUser(user);
			keystore.setCreationDate(new Date());
			// Save created keyStore in database.
			keystore = keystoreService.save(keystore);

			// Save on disk created keyStore.
			keyStore.store(fileOutputStream, keyStorePasswordChars);
		} finally {
			try {
				if (fileOutputStream != null) {
					fileOutputStream.close();
				}
			} catch (Exception e2) {
				// Silent.
			}
		}
	}

	/**
	 * Import a certificate based on his content and certificate key in a new
	 * keyStore generated used provided details such as alias, filename and
	 * password.
	 * 
	 * @param certificatePEM
	 *            The content of certificate and include -----BEGIN CERTIFICATE-----
	 *            and -----END CERTIFICATE-----.
	 * @param privateKeyPEM
	 *            The content of private key and include -----BEGIN RSA PRIVATE
	 *            KEY----- and -----END RSA PRIVATE KEY-----.
	 * @param privateKeyDERStream
	 * @param certificateDERStream
	 * 
	 * @throws Exception
	 *             If a error occurs.
	 */
	public void importCertificate(String certificatePEM, String privateKeyPEM, byte[] certificateDERStream,
			byte[] privateKeyDERStream, User user) throws Exception {
		FileOutputStream fileOutputStream = null;
		try {
			java.security.cert.Certificate[] chain = extractCertificates(certificatePEM, certificateDERStream);
			String commonName = getCommonName((X509Certificate) chain[0]);
			PrivateKey privateKey = extractPrivateKey(privateKeyPEM, privateKeyDERStream);
			KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
			keyStore.load(null);
			String keyStorePassword = stringGeneratorUtils.getRandomString();
			String privateKeyPassword = stringGeneratorUtils.getRandomString();
			char[] privateKeyPasswordChars = privateKeyPassword.toCharArray();

			keyStore.setKeyEntry(commonName, privateKey, privateKeyPasswordChars, chain);
			File keyStoreFile = createKeyStore(commonName, user.getId(), keyStorePassword, true);
			fileOutputStream = new FileOutputStream(keyStoreFile);

			Keystore keystore = new Keystore();
			keystore.setCertificateSubject(commonName);
			keystore.setKeyStorePassword(aesUtils.encryptKeyStorePassword(keyStorePassword));
			keystore.setPrivateKeyPassword(aesUtils.encryptPrivateKeyPassword(privateKeyPassword));
			keystore.setName(keyStoreFile.getName());
			keystore.setUser(user);
			keystore.setCreationDate(new Date());
			keystore = keystoreService.save(keystore);
			char[] passwordChars = keyStorePassword.toCharArray();
			keyStore.store(fileOutputStream, passwordChars);
		} finally {
			try {
				if (fileOutputStream != null) {
					fileOutputStream.close();
				}
			} catch (Exception e) {
				// Silent.
			}
		}
	}

	/**
	 * Extract private key from PEM format or DER format.
	 * 
	 * @param privateKeyPEM
	 *            Private key in PEM format.
	 * @param privateKeyDERStream
	 *            Private key in DER format.
	 * @return extracted private key if is found, null otherwise.
	 * @throws Exception
	 *             If a error occurs.
	 */
	private PrivateKey extractPrivateKey(String privateKeyPEM, byte[] privateKeyDERStream) throws Exception {
		PEMParser parser = null;
		PrivateKey privateKey = null;
		try {
			if (privateKeyPEM != null) {
				parser = new PEMParser(new StringReader(privateKeyPEM));
				Object returnedObject = parser.readObject();
				privateKey = objectToPrivateKey(returnedObject);
			} else {
				PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyDERStream);
				KeyFactory keyFactory = null;
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
			} catch (Exception e) {
				// Do nothing.
			}
		}

		if (privateKey == null) {
			throw new Exception(ErrorMessageBundle.PRIVATE_KEY_NOT_FOUND);
		}

		return privateKey;
	}

	/**
	 * Convert object returned by pem parser in private key object.
	 * 
	 * @param object
	 *            The object who will be converted at private key.
	 * @return resulted private key.
	 * @throws Exception
	 */
	private PrivateKey objectToPrivateKey(Object object) throws Exception {
		PrivateKey privateKey = null;
		if (object != null) {
			if (object instanceof PrivateKeyInfo) {
				PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) object;
				byte[] encoded = privateKeyInfo.getEncoded();
				KeyFactory kf = null;
				try {
					kf = KeyFactory.getInstance(RSA_ALGORITHM_NAME);
					privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(encoded));
				} catch (InvalidKeySpecException e) {
					kf = KeyFactory.getInstance(ELLIPTIC_CURVES_ALGORITHM_NAME);
					privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(encoded));
				}
			} else if (object instanceof PEMKeyPair) {
				PEMKeyPair pemKeyPair = (PEMKeyPair) object;
				privateKey = new JcaPEMKeyConverter().getPrivateKey(pemKeyPair.getPrivateKeyInfo());
			}
		}

		return privateKey;
	}

	/**
	 * Extract certificate chain from PEM format or DER format.
	 * 
	 * @param certificatePEM
	 *            Certificates in PEM format.(String)
	 * @param certificateDER
	 *            Certificates in DER format.(Binary)
	 * 
	 * @return extracted chain from provided data, null otherwise.
	 * @throws Exception
	 *             If a error occurs.
	 */
	private java.security.cert.Certificate[] extractCertificates(String certificatePEM, byte[] certificateDER)
			throws Exception {
		java.security.cert.Certificate[] chain = null;
		InputStream inputStream = null;
		try {
			if (certificatePEM != null) {
				inputStream = new ByteArrayInputStream(certificatePEM.getBytes());
			} else {
				inputStream = new ByteArrayInputStream(certificateDER);
			}

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
				// Check if introduced certificate is valid. If is not then a
				// exception is thrown and the message will be visible in
				// interface.
				cert509.checkValidity();
				chain[counter] = cert509;
				counter++;
			}
			return chain;
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	/**
	 * Get common name for given certificate as string.
	 * 
	 * @param x509Certificate
	 *            The certificate from we extract common name.
	 * @return common name of given certificate.
	 * @throws Exception
	 */
	private String getCommonName(X509Certificate x509Certificate) throws Exception {
		String commonName = null;
		try {
			X500Name x500name = new JcaX509CertificateHolder(x509Certificate).getSubject();
			RDN cn = x500name.getRDNs(BCStyle.CN)[0];
			commonName = IETFUtils.valueToString(cn.getFirst().getValue());
		} catch (Exception e) {
			throw new Exception(ErrorMessageBundle.INVALID_CERTIFICATE);
		}

		return commonName;
	}

	/**
	 * Create a empty keyStore file and throw exception if file already exist.
	 * 
	 * @param keyStoreName
	 *            The name of keyStore file.
	 * @param keyStorePassword
	 *            The password of keyStore.
	 * @return created keyStore file.
	 * @throws Exception
	 *             If keyStore file already exist.
	 */
	private File createKeyStore(String keyStoreName, String userID, String keyStorePassword, boolean create)
			throws Exception {
		keyStoreName = folderUtils.getFileNameForWindows(keyStoreName);
		keyStoreName = keyStoreName + "_" + System.currentTimeMillis() + "_"
				+ stringGeneratorUtils.generateRandomFileName() + ".keystore";

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
	 * @param certificateValidity
	 *            Validity number. Represents the number of days, months or years.
	 * @param certificateValidityType
	 *            Represent the type of validity and values can be: year, month or
	 *            day.
	 * @return expired date for a certificate.
	 * @throws Exception
	 *             If difference between current date and resulted date is more than
	 *             25 years.
	 */
	private Date calculateNotAfter(int certificateValidity, String certificateValidityType) {
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
	 * @param certificateParameters
	 *            The certificate parameters such as OU, O etc.
	 * @return a object formed by given parameters.
	 */
	private X500Name getX500Name(Certificate certificate) {

		X500NameBuilder nameBuilder = new X500NameBuilder(BCStyle.INSTANCE);
		// I don't made test if common name is null because this field is
		// mandatory.
		nameBuilder.addRDN(BCStyle.CN, certificate.getCommonName());

		String organizationUnit = certificate.getOrganizationUnit();
		if (organizationUnit != null && organizationUnit.trim().length() > 0) {
			nameBuilder.addRDN(BCStyle.OU, organizationUnit);
		}

		String organization = certificate.getOrganization();
		if (organization != null && organization.trim().length() > 0) {
			nameBuilder.addRDN(BCStyle.O, organization);
		}

		String locality = certificate.getLocality();
		if (locality != null && locality.trim().length() > 0) {
			nameBuilder.addRDN(BCStyle.L, locality);
		}

		String state = certificate.getState();
		if (state != null && state.trim().length() > 0) {
			nameBuilder.addRDN(BCStyle.ST, state);
		}

		String country = certificate.getCountry();
		if (country != null && country.trim().length() > 0) {
			nameBuilder.addRDN(BCStyle.C, country);
		}

		String email = certificate.getEmail();
		if (email != null && email.trim().length() > 0) {
			nameBuilder.addRDN(BCStyle.E, email);
		}

		return nameBuilder.build();
	}

	/**
	 * Get certificate information as a XML structure.
	 * 
	 * @param document
	 *            Used to create tags.
	 * @param foundedKeystore
	 *            The certificate from who extract necessary information.
	 * @param user
	 *            Certificate alias.
	 * 
	 * @return Element with certificate info as a XML structure.
	 */
	public List<CertificateDetails> getCertificatesInfo(Keystore foundedKeystore, User user) throws Exception {
		File keyStoreFile = getKeyStoreFile(user.getId(), foundedKeystore.getName());
		String keyStorePassword = aesUtils.decryptKeyStorePassword(foundedKeystore.getKeyStorePassword());
		KeyStore keyStore = loadKeyStoreFromDisk(keyStoreFile.getAbsolutePath(), keyStorePassword);
		Enumeration<String> aliases = keyStore.aliases();
		if (aliases == null) {
			throw new Exception(ErrorMessageBundle.CERTIFICATE_NOT_FOUND);
		}

		List<CertificateDetails> certificatesDetails = new ArrayList<>();
		String privateKeyPassword = aesUtils.decryptPrivateKeyPassword(foundedKeystore.getPrivateKeyPassword());
		while (aliases.hasMoreElements()) {
			String certificateAlias = (String) aliases.nextElement();
			java.security.cert.Certificate certificates[] = keyStore.getCertificateChain(certificateAlias);
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

	/**
	 * 
	 * @param certificate
	 * @param key
	 * @param certificateAlias
	 * @return
	 * @throws Exception
	 */
	private CertificateDetails getCertificateInformation(java.security.cert.Certificate certificate, Key key,
			String certificateAlias) throws Exception {
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
			if (key != null && key instanceof PrivateKey) {
				certificateDetails.setKeySize(getPrivateKeySize((PrivateKey) key));
			}

			try {
				certificateDetails.setPem(getPemValue(x509Certificate));
			} catch (Exception e) {
				// Do nothing.
			}

			Date notBefore = x509Certificate.getNotBefore();
			Date notAfter = x509Certificate.getNotAfter();
			certificateDetails.setNotAfter(df.format(notAfter));
			certificateDetails.setNotBefore(df.format(notBefore));
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

	/**
	 * Method used to retrieve encoded form of a certificate as string.
	 * 
	 * @param xcert
	 *            The certificate from who will extract the encoded form of this
	 *            certificate.
	 * 
	 * @return PEM Certificate as string.
	 * @throws IOException
	 *             If a error is thrown.
	 */
	private String getPemValue(X509Certificate xcert) throws IOException {
		StringWriter stringWriter = null;
		JcaPEMWriter jcaPemWriter = null;
		try {
			stringWriter = new StringWriter();
			jcaPemWriter = new JcaPEMWriter(stringWriter);
			jcaPemWriter.writeObject(xcert);
			jcaPemWriter.flush();
		} finally {
			try {
				if (jcaPemWriter != null) {
					jcaPemWriter.close();
				}
			} catch (Exception e) {
				// Silent.
			}
		}

		return stringWriter.toString();
	}

	private String getPemValuePrivateKey(Key key) throws Exception {
		StringWriter stringWriter = null;
		JcaPEMWriter jcaPemWriter = null;
		try {
			stringWriter = new StringWriter();
			jcaPemWriter = new JcaPEMWriter(stringWriter);
			jcaPemWriter.writeObject(key);
			jcaPemWriter.flush();
		} finally {
			try {
				if (jcaPemWriter != null) {
					jcaPemWriter.close();
				}
			} catch (Exception e) { // Silent. }
			}
		}

		return stringWriter.toString();
	}

	/**
	 * Check if a certificate is self signed.
	 * 
	 * @param x509Certificate
	 *            Certificate who will be checked if is self signed.
	 * 
	 * @return <b>true</b> if certificate is self signed, <b>false</b> otherwise.
	 */
	private boolean isSelfSigned(X509Certificate x509Certificate) {
		Principal issuerDN = x509Certificate.getIssuerDN();
		Principal subjectDN = x509Certificate.getSubjectDN();

		return issuerDN.equals(subjectDN);
	}

	/**
	 * Get issuer for given certificate as string.
	 * 
	 * @param x509Certificate
	 *            The certificate from we extract subject.
	 * @return issuer of given certificate.
	 */
	private String getIssuer(X509Certificate x509Certificate) {
		String issuerDnString = null;
		JcaX509CertificateHolder certHolder;
		try {
			certHolder = new JcaX509CertificateHolder(x509Certificate);
			issuerDnString = certHolder.getIssuer().toString();
		} catch (CertificateEncodingException e) {
			// Silent.
		}

		if (issuerDnString == null) {
			Map<String, String> additionalOIDs = new HashMap<String, String>();
			additionalOIDs.put(BCStyle.E.getId(), "E");
			issuerDnString = x509Certificate.getIssuerX500Principal().getName(X500Principal.RFC2253, additionalOIDs);
		}

		return issuerDnString;
	}

	/**
	 * Get subject for given certificate as string.
	 * 
	 * @param x509Certificate
	 *            The certificate from we extract subject.
	 * @return subject of given certificate.
	 */
	private String getSubject(X509Certificate x509Certificate) {
		String subjectDnString = null;

		JcaX509CertificateHolder certHolder;
		try {
			certHolder = new JcaX509CertificateHolder(x509Certificate);
			subjectDnString = certHolder.getSubject().toString();
		} catch (CertificateEncodingException e) {
			// Silent.
		}

		if (subjectDnString == null) {
			Map<String, String> additionalOIDs = new HashMap<String, String>();
			additionalOIDs.put(BCStyle.E.getId(), "E");
			subjectDnString = x509Certificate.getSubjectX500Principal().getName(X500Principal.RFC2253, additionalOIDs);
		}

		return subjectDnString;
	}

	/**
	 * Load a KeyStore from disk based on full path and password.
	 * 
	 * @param keyStoreFullPath
	 *            Full path to KeyStore.
	 * @param keyStorePassword
	 *            Password for KeyStore.
	 * 
	 * @return KeyStore object who represents the file from disk.
	 * 
	 * @throws Exception
	 *             If a error occurs.
	 */
	private KeyStore loadKeyStoreFromDisk(String keyStoreFullPath, String keyStorePassword) throws Exception {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(keyStoreFullPath);
			return loadKeyStore(inputStream, keyStorePassword);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				// Stay silent.
			}
		}
	}

	/**
	 * Load a KeyStore from disk based on full path and password.
	 * 
	 * @param keyStoreFullPath
	 *            Full path to KeyStore.
	 * @param keyStorePassword
	 *            Password for KeyStore.
	 * 
	 * @return KeyStore object who represents the file from disk.
	 * 
	 * @throws Exception
	 *             If a error occurs when we try to load keyStore.
	 */
	private KeyStore loadKeyStore(InputStream inputStream, String keyStorePassword) throws Exception {
		KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
		keyStore.load(inputStream, keyStorePassword.toCharArray());

		return keyStore;
	}

	/**
	 * This method gets a key and computes the size of the key. The key can have
	 * more formats as is an interface for multiple key types.
	 * <p/>
	 * This method handles: RSAKey, DSAKey and DHKey.
	 *
	 * @param key
	 *            The public key for which the size needs to be calculated.
	 * @return An integer representing the size of the key.
	 */
	private int getPrivateKeySize(final PrivateKey key) {
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

	/**
	 * Generate CSR based on certificate and return generated CSR as string.
	 * 
	 * @param certificate
	 *            A certificate on the basis of which it is generated the CSR.
	 * @param key
	 *            Private key from keyStore used to get information such as
	 *            algorithm used and key size.
	 * 
	 * @return generated CSR as string.
	 * 
	 * @throws Exception
	 *             If a error occurs.
	 */
	private InputStream generateCSR(java.security.cert.Certificate certificate, Key key) throws Exception {
		JcaPEMWriter jcaPEMWriter = null;
		StringWriter stringWriter = null;
		try {
			if (certificate instanceof X509Certificate) {
				X509Certificate cert = (X509Certificate) certificate;
				PublicKey publicKey = cert.getPublicKey();

				JcaX509CertificateHolder certificateHolder = new JcaX509CertificateHolder(cert);
				X500Name subjectX500Name = certificateHolder.getSubject();
				if (subjectX500Name == null) {
					throw new CertificateEncodingException(ErrorMessageBundle.CERTIFICATE_SUBJECT_NOT_FOUND);
				}

				ContentSigner contentSigner = new JcaContentSignerBuilder(cert.getSigAlgName()).build((PrivateKey) key);
				PKCS10CertificationRequestBuilder builder = new JcaPKCS10CertificationRequestBuilder(subjectX500Name,
						publicKey);
				PKCS10CertificationRequest csr = builder.build(contentSigner);

				stringWriter = new StringWriter();
				jcaPEMWriter = new JcaPEMWriter(stringWriter);
				jcaPEMWriter.writeObject(csr);
				jcaPEMWriter.flush();

				return new ByteArrayInputStream(stringWriter.toString().getBytes());
			} else {
				throw new Exception(ErrorMessageBundle.UNSUPPORTED_CERTIFICATE_FORMAT);
			}
		} finally {
			try {
				if (jcaPEMWriter != null) {
					jcaPEMWriter.close();
				}
			} catch (Exception e) {
				// Silent.
			}
		}
	}

	public InputStream generateCSR(Keystore foundedKeystore, User user) throws Exception {
		File keyStoreFile = getKeyStoreFile(user.getId(), foundedKeystore.getName());
		String keyStorePassword = aesUtils.decryptKeyStorePassword(foundedKeystore.getKeyStorePassword());
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
		String privateKeyPassword = aesUtils.decryptPrivateKeyPassword(foundedKeystore.getPrivateKeyPassword());
		Key key = keyStore.getKey(certificateAlias, privateKeyPassword.toCharArray());

		if (certificate == null || key == null) {
			throw new NotFoundException(ErrorMessageBundle.CERTIFICATE_NOT_FOUND);
		}
		return generateCSR(certificate, key);
	}

	/**
	 * 
	 * @param userId
	 * @param keyStoreFileName
	 * @return
	 * @throws Exception
	 */
	public File getKeyStoreFile(String userId, String keyStoreFileName) throws Exception {
		File userKeyStoreDirectory = folderUtils.getKeystoresFolderFile(userId);
		File keyStoreFile = new File(userKeyStoreDirectory, keyStoreFileName);
		if (!keyStoreFile.exists()) {
			throw new Exception(ErrorMessageBundle.CERTIFICATE_NOT_FOUND);
		}

		return keyStoreFile;
	}

	public InputStream exportCertificate(Keystore foundedKeystore, User user) throws Exception {
		File keyStoreFile = getKeyStoreFile(user.getId(), foundedKeystore.getName());
		String keyStorePassword = aesUtils.decryptKeyStorePassword(foundedKeystore.getKeyStorePassword());
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
		return new ByteArrayInputStream(getPemValue((X509Certificate) certificate).getBytes());
	}

	/**
	 * Export private key in PEM format.
	 * 
	 * @param foundedKeystore
	 *            Find keyStore from database.
	 * @param user
	 *            The authenticated user.
	 * @return a stream with private key in PEM format.
	 * @throws Exception
	 *             If a error occurs.
	 */
	public InputStream export_privateKey(Keystore foundedKeystore, User user, Boolean asPEM) throws Exception {
		Key key = getPrivateKey(foundedKeystore, user);
		if (asPEM != null && asPEM) {
			return new ByteArrayInputStream(getPemValuePrivateKey(key).getBytes());
		}

		return new ByteArrayInputStream(key.getEncoded());
	}

	public Key getPrivateKey(Keystore foundedKeystore, User user) throws Exception {
		String keyStorePassword = aesUtils.decryptKeyStorePassword(foundedKeystore.getKeyStorePassword());
		File keyStoreFile = getKeyStoreFile(user.getId(), foundedKeystore.getName());
		KeyStore keyStore = loadKeyStoreFromDisk(keyStoreFile.getAbsolutePath(), keyStorePassword);
		Enumeration<String> aliases = keyStore.aliases();
		if (aliases == null) {
			throw new Exception(ErrorMessageBundle.CERTIFICATE_NOT_FOUND);
		}
		String certificateAlias = aliases.nextElement();
		if (certificateAlias == null) {
			throw new Exception(ErrorMessageBundle.CERTIFICATE_NOT_FOUND);
		}
		String privateKeyPassword = aesUtils.decryptPrivateKeyPassword(foundedKeystore.getPrivateKeyPassword());
		Key key = keyStore.getKey(certificateAlias, privateKeyPassword.toCharArray());

		return key;
	}

	/**
	 * Upload a certificate using files.
	 * 
	 * @param certificate
	 * @param privateKey
	 * @param user
	 * @throws Exception
	 */
	public void uploadCertificate(MultipartFile certificate, MultipartFile privateKey, User user) throws Exception {
		if (certificate.isEmpty() && privateKey.isEmpty()) {
			throw new Exception("You must provide both files.");
		}

		importCertificate(null, null, certificate.getBytes(), privateKey.getBytes(), user);
	}

	public void deleteCertificate(User user, String id) {

	}

	/**
	 * Convert private key text to private key object.
	 * 
	 * @param privateKeyText
	 *            The private key who will be converted.
	 * @return obtained private key object.
	 * @throws Exception
	 *             If a error occurs.
	 * 
	 *             private PrivateKey textToPrivateKey(String privateKeyText) throws
	 *             Exception { PEMParser parser = null; try { parser = new
	 *             PEMParser(new StringReader(privateKeyText)); Object o =
	 *             parser.readObject(); if (o == null || !(o instanceof PEMKeyPair))
	 *             { throw new IOException("The private key is invalid."); }
	 *             PEMKeyPair pemKeyPair = (PEMKeyPair) o; PrivateKey privateKey =
	 *             new
	 *             JcaPEMKeyConverter().getPrivateKey(pemKeyPair.getPrivateKeyInfo());
	 *             return privateKey; } finally { try { if (parser != null) {
	 *             parser.close(); } } catch (Exception e) { // Do nothing. } } }
	 */

	/**
	 * Sign a document using provided private key.
	 * 
	 * @param documentToSign
	 *            The document who will be signed.
	 * @param privateKeyFile
	 *            The key used to sign document.
	 * @return Signed document.
	 * @throws Exception
	 */
	public byte[] signDocument(byte[] documentToSign, PrivateKey privateKey, String algorithm) throws Exception {
		Signature signature = Signature.getInstance(algorithm);
		signature.initSign(privateKey);
		signature.update(documentToSign);

		return signature.sign();
	}

	/**
	 * 
	 * @param signatureData
	 * @param publicKey
	 * @param algorithm
	 * @return
	 * @throws Exception
	 */
	public boolean verifyDocumentSignature(byte[] signatureData, byte[] documentData,
			java.security.cert.Certificate certificate) throws Exception {
		Signature signature = Signature.getInstance(((X509Certificate) certificate).getSigAlgName());
		signature.initVerify(certificate.getPublicKey());
		signature.update(documentData);
		return signature.verify(signatureData);
	}

	/**
	 * Retrieve certificates and save in a new keystore.
	 * 
	 * @param aURL
	 *            The URL from certificates will be retrieved.
	 * @throws Exception
	 *             If a error occurs.
	 */
	public List<CertificateDetails> retrieveCertificates(URL destinationURL) throws Exception {
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

	public KeyStore getKeyStore(Keystore foundedKeystore, User user) throws Exception {
		String keyStorepassword = aesUtils.decryptKeyStorePassword(foundedKeystore.getKeyStorePassword());
		File keyStoreFile = getKeyStoreFile(user.getId(), foundedKeystore.getName());
		KeyStore keyStore = loadKeyStoreFromDisk(keyStoreFile.getAbsolutePath(), keyStorepassword);
		if (keyStore == null) {
			throw new NotFoundException(ErrorMessageBundle.CERTIFICATE_NOT_FOUND);
		}
		return keyStore;
	}

	public java.security.cert.Certificate getCertificate(Keystore foundedKeystore, User user) throws Exception {
		KeyStore keyStore = getKeyStore(foundedKeystore, user);
		return extractCertificateFromKeystore(keyStore);
	}

	public String getAlias(KeyStore keyStore) throws KeyStoreException {
		Enumeration<String> aliases = keyStore.aliases();
		if (aliases == null) {
			throw new NotFoundException(ErrorMessageBundle.CERTIFICATE_NOT_FOUND);
		}
		String certificateAlias = aliases.nextElement();
		if (certificateAlias == null) {
			throw new NotFoundException(ErrorMessageBundle.CERTIFICATE_NOT_FOUND);
		}

		return certificateAlias;
	}

	public java.security.cert.Certificate extractCertificateFromKeystore(KeyStore keyStore) throws KeyStoreException {
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
		if (certificate == null && !(certificate instanceof X509Certificate)) {
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
						success = IOUtils.contentEquals(new FileInputStream(documentFile),
								signedDocument.getInputStream());
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
