package ro.certificate.manager.wrapper;

import org.hibernate.validator.constraints.NotEmpty;

public class ImportCertificate {

	@NotEmpty
	private String certificate;

	@NotEmpty
	private String privateKey;

	public String getCertificate() {
		return certificate;
	}

	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

}
