package ro.certificate.manager.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotEmpty;

@Table
@Entity
public class Signature implements Serializable {

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
	private Document document;

	@OneToOne
	private Keystore keystore;

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

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Keystore getKeystore() {
		return keystore;
	}

	public void setKeystore(Keystore keystore) {
		this.keystore = keystore;
	}
}
