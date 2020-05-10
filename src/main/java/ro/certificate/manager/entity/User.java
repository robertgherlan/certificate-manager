package ro.certificate.manager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import ro.certificate.manager.annotation.UniqueEmail;
import ro.certificate.manager.annotation.UniqueUsername;
import ro.certificate.manager.utils.ErrorMessageBundle;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table
public class User implements Serializable {
	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 9093304836112847216L;

	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@Column
	@Id
	private String id;

	@UniqueUsername(message = ErrorMessageBundle.USERNAME_ALREADY_EXIST)
	@NotNull(message = ErrorMessageBundle.INVALID_FIELD)
	@NotEmpty(message = ErrorMessageBundle.INVALID_FIELD)
	private String username;

	@UniqueEmail(message = ErrorMessageBundle.EMAIL_ALREADY_EXIST)
	@Email(message = ErrorMessageBundle.INVALID_FIELD)
	@NotNull(message = ErrorMessageBundle.INVALID_FIELD)
	@NotEmpty(message = ErrorMessageBundle.INVALID_FIELD)
	private String email;

	@NotNull(message = ErrorMessageBundle.INVALID_FIELD)
	@NotEmpty(message = ErrorMessageBundle.INVALID_FIELD)
	private String firstname;

	@NotNull(message = ErrorMessageBundle.INVALID_FIELD)
	@NotEmpty(message = ErrorMessageBundle.INVALID_FIELD)
	private String lastname;

	@JsonIgnore
	@NotNull(message = ErrorMessageBundle.INVALID_FIELD)
	@NotEmpty(message = ErrorMessageBundle.INVALID_FIELD)
	@Size(min = 8, max = 256, message = "Password must have between 8 and 256 characters.")
	private String password;

	@JsonIgnore
	@Column(unique = true)
	private String registerToken;

	@JsonIgnore
	@Column(unique = true)
	private String recoverPasswordToken;

	@JsonIgnore
	private Date creationDate;

	@JsonIgnore
	private Date expiredDate;

	@JsonIgnore
	private boolean expired = false;

	@JsonIgnore
	private boolean enabled = false;

	@JsonIgnore
	private boolean validEmail = false;

	@JsonIgnore
	private String registerIpAddress = null;

	@ManyToMany(fetch = FetchType.EAGER)
	@LazyCollection(LazyCollectionOption.TRUE)
	@JoinTable
	private List<Role> roles;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@LazyCollection(LazyCollectionOption.TRUE)
	private List<Keystore> keyStores;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@LazyCollection(LazyCollectionOption.TRUE)
	private List<Document> documents;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getRegisterToken() {
		return registerToken;
	}

	public void setRegisterToken(String registerToken) {
		this.registerToken = registerToken;
	}

	public String getRecoverPasswordToken() {
		return recoverPasswordToken;
	}

	public void setRecoverPasswordToken(String recoverPaswordToken) {
		this.recoverPasswordToken = recoverPaswordToken;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public boolean isExpired() {
		return expired;
	}

	public void setExpired(boolean expired) {
		this.expired = expired;
	}

	public Date getExpiredDate() {
		return expiredDate;
	}

	public void setExpiredDate(Date expiredDate) {
		this.expiredDate = expiredDate;
	}

	public List<Keystore> getKeyStores() {
		return keyStores;
	}

	public void setKeyStores(List<Keystore> keyStores) {
		this.keyStores = keyStores;
	}

	public boolean isValidEmail() {
		return validEmail;
	}

	public void setValidEmail(boolean validEmail) {
		this.validEmail = validEmail;
	}

	public String getRegisterIpAddress() {
		return registerIpAddress;
	}

	public void setRegisterIpAddress(String registerIpAddress) {
		this.registerIpAddress = registerIpAddress;
	}

	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

}
