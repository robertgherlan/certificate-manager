package ro.certificate.manager.wrapper;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;

public class Certificate {

	@NotEmpty
	private String commonName;

	private String organization;

	private String organizationUnit;

	private String country;

	private String state;

	private String email;

	private String locality;

	@Min(1)
	private int validityNumber;

	@NotEmpty
	private String validityType;

	@NotEmpty
	private String encryptionAlgorithm;

	@NotEmpty
	private String signatureAlgorithm;

	@Min(128)
	private int keySize;

	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getOrganizationUnit() {
		return organizationUnit;
	}

	public void setOrganizationUnit(String organizationUnit) {
		this.organizationUnit = organizationUnit;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public int getValidityNumber() {
		return validityNumber;
	}

	public void setValidityNumber(int validityNumber) {
		this.validityNumber = validityNumber;
	}

	public String getValidityType() {
		return validityType;
	}

	public void setValidityType(String validityType) {
		this.validityType = validityType;
	}

	public String getEncryptionAlgorithm() {
		return encryptionAlgorithm;
	}

	public void setEncryptionAlgorithm(String encryptionAlgorithm) {
		this.encryptionAlgorithm = encryptionAlgorithm;
	}

	public String getSignatureAlgorithm() {
		return signatureAlgorithm;
	}

	public void setSignatureAlgorithm(String signatureAlgorithm) {
		this.signatureAlgorithm = signatureAlgorithm;
	}

	public int getKeySize() {
		return keySize;
	}

	public void setKeySize(int keySize) {
		this.keySize = keySize;
	}

}
