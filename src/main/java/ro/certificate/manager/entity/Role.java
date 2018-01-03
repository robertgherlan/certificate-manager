package ro.certificate.manager.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class Role implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 3170108542930855678L;

	@Id
	@GeneratedValue
	private Long id;

	@Column(unique = true)
	private String name;

	@ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
	private List<User> users;

	public Role() {
	}

	public Role(String name) {
		super();
		this.name = name;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}