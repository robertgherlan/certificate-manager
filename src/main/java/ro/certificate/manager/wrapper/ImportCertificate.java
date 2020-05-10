package ro.certificate.manager.wrapper;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class ImportCertificate {

	@NotEmpty
	private String certificate;

	@NotEmpty
	private String privateKey;
}
