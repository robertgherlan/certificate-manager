package ro.certificate.manager.wrapper;

public class CertificateDetails {

	private String commonName;

	private String subject;

	private String issuer;

	private String alias;

	private String format;

	private boolean expired;

	private int keySize;

	private String pem;

	private String asn1;

	private boolean selfSigned;

	private String notAfter;

	private String notBefore;

	private String signatureAlgorithm;

	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public boolean isExpired() {
		return expired;
	}

	public void setExpired(boolean expired) {
		this.expired = expired;
	}

	public int getKeySize() {
		return keySize;
	}

	public void setKeySize(int keySize) {
		this.keySize = keySize;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getPem() {
		return pem;
	}

	public void setPem(String pem) {
		this.pem = pem;
	}

	public boolean isSelfSigned() {
		return selfSigned;
	}

	public void setSelfSigned(boolean selfSigned) {
		this.selfSigned = selfSigned;
	}

	public String getNotAfter() {
		return notAfter;
	}

	public void setNotAfter(String notAfter) {
		this.notAfter = notAfter;
	}

	public String getNotBefore() {
		return notBefore;
	}

	public void setNotBefore(String notBefore) {
		this.notBefore = notBefore;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getSignatureAlgorithm() {
		return signatureAlgorithm;
	}

	public void setSignatureAlgorithm(String signatureAlgorithm) {
		this.signatureAlgorithm = signatureAlgorithm;
	}

	public String getAsn1() {
		return asn1;
	}

	public void setAsn1(String asn1) {
		this.asn1 = asn1;
	}

}
