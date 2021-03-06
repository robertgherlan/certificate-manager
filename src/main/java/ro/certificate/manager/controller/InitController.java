package ro.certificate.manager.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ro.certificate.manager.entity.Role;
import ro.certificate.manager.entity.User;
import ro.certificate.manager.utils.StringGeneratorUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Controller
public class InitController extends BaseController {

	private void initDatabase() {
		List<Role> userRoles = new ArrayList<>();
		userRoles.add(roleService.findUserRole());

		List<Role> adminRoles = new ArrayList<>();
		adminRoles.add(roleService.findUserRole());
		adminRoles.add(roleService.findAdminRole());

		for (int i = 0; i < 200; i++) {
			User user = new User();
			user.setCreationDate(new Date());
			user.setEmail("user" + i + "@gmail.com");
			user.setValidEmail(new Random().nextBoolean());
			user.setEnabled(new Random().nextBoolean());
			user.setExpired(new Random().nextBoolean());
			user.setExpiredDate(new Date());
			user.setRoles(userRoles);
			user.setPassword(new BCryptPasswordEncoder().encode("user" + i));
			user.setUsername("user" + i);
			user.setFirstname(StringGeneratorUtils.getUsernameString());
			user.setLastname(StringGeneratorUtils.getUsernameString());
			user.setRecoverPasswordToken(StringGeneratorUtils.getRandomString());
			user.setRegisterToken(StringGeneratorUtils.getRandomString());
			userService.saveAndFlush(user);
		}

		for (int i = 0; i < 100; i++) {
			User user = new User();
			user.setCreationDate(new Date());
			user.setEmail("admin" + i + "@gmail.com");
			user.setValidEmail(new Random().nextBoolean());
			user.setEnabled(new Random().nextBoolean());
			user.setExpired(new Random().nextBoolean());
			user.setExpiredDate(new Date());
			user.setRoles(adminRoles);
			user.setPassword(new BCryptPasswordEncoder().encode("admin" + i));
			user.setUsername("admin" + i);
			user.setRecoverPasswordToken(StringGeneratorUtils.getRandomString());
			user.setRegisterToken(StringGeneratorUtils.getRandomString());
			user.setFirstname(StringGeneratorUtils.getUsernameString());
			user.setLastname(StringGeneratorUtils.getUsernameString());
			userService.saveAndFlush(user);
		}
	}

	@RequestMapping(value = "/init", method = RequestMethod.GET)
	public String addUsers() {
		try {
			if (userService.findAll().isEmpty()) {
				initDatabase();
			}
		} catch (Exception e) {
			initDatabase();
		}
		return "/home";
	}
}
