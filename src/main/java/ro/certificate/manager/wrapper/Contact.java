package ro.certificate.manager.wrapper;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

public class Contact {

	@NotEmpty
	@Size(min = 3, max = 50)
	@Email
	private String email;

	@NotEmpty
	@Size(min = 3, max = 50)
	private String name;

	@NotEmpty
	@Size(min = 9, max = 20)
	private String phone;

	@NotEmpty
	@Size(min = 3, max = 500)
	private String message;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
