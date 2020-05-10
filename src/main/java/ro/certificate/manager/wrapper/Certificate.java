package ro.certificate.manager.wrapper;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Data
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
}
