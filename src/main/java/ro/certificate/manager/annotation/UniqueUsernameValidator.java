package ro.certificate.manager.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import ro.certificate.manager.repository.UserRepository;

public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {

	@Autowired
	private UserRepository userRepository;

	@Override
	public void initialize(UniqueUsername constraintAnnotation) {
	}

	@Override
	public boolean isValid(String username, ConstraintValidatorContext context) {
		if (userRepository == null) {
			return true;
		}
		return userRepository.findByUsername(username) == null;
	}

}