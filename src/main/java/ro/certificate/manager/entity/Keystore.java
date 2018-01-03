package ro.certificate.manager.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table
public class Keystore implements Serializable {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8856907110610641L;

	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@Column
	@Id
	private String id;

	@NotNull
	@NotEmpty
	private String name;

	@JsonIgnore
	@NotEmpty
	private String keyStorePassword;

	@JsonIgnore
	@NotEmpty
	private String privateKeyPassword;

	@NotEmpty
	private String certificateSubject;

	@NotNull
	private Date creationDate;

	@JsonIgnore
	private Date expirationDate;

	@JsonIgnore
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "user_id")
	private User user;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKeyStorePassword() {
		return keyStorePassword;
	}

	public void setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}

	public String getCertificateSubject() {
		return certificateSubject;
	}

	public void setCertificateSubject(String certificateSubject) {
		this.certificateSubject = certificateSubject;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getPrivateKeyPassword() {
		return privateKeyPassword;
	}

	public void setPrivateKeyPassword(String privateKeyPassword) {
		this.privateKeyPassword = privateKeyPassword;
	}

}
