package ro.certificate.manager.wrapper;



import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import ro.certificate.manager.annotation.FieldMatch;
import ro.certificate.manager.annotation.UniqueEmail;
import ro.certificate.manager.annotation.UniqueUsername;
import ro.certificate.manager.utils.ErrorMessageBundle;

@FieldMatch.List({
		@FieldMatch(first = "password", second = "confirmPassword", message = "The password fields must match"),
		@FieldMatch(first = "email", second = "confirmEmail", message = "The email fields must match") })
public class UserRegistrationForm {

	@NotNull
	@NotEmpty
	private String firstName;

	@NotNull
	@NotEmpty
	private String lastName;

	@UniqueUsername(message = ErrorMessageBundle.USERNAME_ALREADY_EXIST)
	@Size(min = 8, max = 50)
	private String username;

	@Size(min = 8, max = 50)
	private String password;

	@Size(min = 8, max = 50)
	private String confirmPassword;

	@NotEmpty
	@Email
	@UniqueEmail(message = ErrorMessageBundle.EMAIL_ALREADY_EXIST)
	private String email;

	@NotEmpty
	@Email
	private String confirmEmail;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getConfirmEmail() {
		return confirmEmail;
	}

	public void setConfirmEmail(String confirmEmail) {
		this.confirmEmail = confirmEmail;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

}