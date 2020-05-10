package ro.certificate.manager.wrapper;

import lombok.Data;

@Data
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

}
