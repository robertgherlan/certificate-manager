package ro.certificate.manager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController extends BaseController {

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home() {
		return "/home";
	}

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String adminHome() {
		return home();
	}
}
