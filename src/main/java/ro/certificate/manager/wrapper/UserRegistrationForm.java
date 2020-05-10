package ro.certificate.manager.wrapper;


import lombok.Data;
import ro.certificate.manager.annotation.FieldMatch;
import ro.certificate.manager.annotation.UniqueEmail;
import ro.certificate.manager.annotation.UniqueUsername;
import ro.certificate.manager.utils.ErrorMessageBundle;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@FieldMatch.List({@FieldMatch(first = "password", second = "confirmPassword", message = "The password fields must match"), @FieldMatch(first = "email", second = "confirmEmail", message = "The email fields must match")})
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
}