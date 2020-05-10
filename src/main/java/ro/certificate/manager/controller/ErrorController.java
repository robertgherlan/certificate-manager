package ro.certificate.manager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ErrorController {

    @RequestMapping(value = "/404", method = RequestMethod.GET)
    public String error_404() {
        return "/404";
    }

    @RequestMapping(value = "/503", method = RequestMethod.GET)
    public String error_503() {
        return "/503";
    }
}