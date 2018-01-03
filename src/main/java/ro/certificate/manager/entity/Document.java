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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotEmpty;

@Table
@Entity
public class Document implements Serializable {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 9011301425697541713L;

	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@Column
	@Id
	private String id;

	@NotEmpty
	private String name;

	@NotEmpty
	private String path;

	@NotNull
	private Date creationDate;

	@OneToOne
	private Keystore keystore;

	@OneToOne
	private Signature signature;

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

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Keystore getKeystore() {
		return keystore;
	}

	public void setKeystore(Keystore keystore) {
		this.keystore = keystore;
	}

	public Signature getSignature() {
		return signature;
	}

	public void setSignature(Signature signature) {
		this.signature = signature;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
