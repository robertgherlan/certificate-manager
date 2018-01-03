package ro.certificate.manager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController extends BaseController {

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String loginGET(Model model) {
		model.addAttribute("captchaSiteKey", configurationUtils.getCaptchaSiteKey());
		return "/login";
	}

	@RequestMapping(value = "/login_spring_security", method = RequestMethod.GET)
	public String loginSpringSecurityGET() {
		return "redirect:/login";
	}
}
