package ro.certificate.manager.wrapper;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
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
}
