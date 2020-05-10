package ro.certificate.manager.controller;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ro.certificate.manager.entity.Role;
import ro.certificate.manager.entity.User;
import ro.certificate.manager.exceptions.ReCaptchaInvalidException;
import ro.certificate.manager.exceptions.ReCaptchaUnavailableException;
import ro.certificate.manager.utils.RequestUtils;
import ro.certificate.manager.utils.StringGeneratorUtils;
import ro.certificate.manager.wrapper.UserRegistrationForm;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
public class RegisterController extends BaseController {

    private static final String REGISTER_PAGE = "/register";

    private static final Logger logger = Logger.getLogger(RegisterController.class);

    @RequestMapping(value = REGISTER_PAGE, method = RequestMethod.GET)
    public String addUserPage(Model model) {
        model.addAttribute("captchaSiteKey", configurationUtils.getCaptchaSiteKey());
        model.addAttribute("registerUser", new UserRegistrationForm());
        return REGISTER_PAGE;
    }

    @RequestMapping(value = REGISTER_PAGE, method = RequestMethod.POST)
    public String addUserPagePost(Model model, @Valid @ModelAttribute("registerUser") UserRegistrationForm registerUser, BindingResult bindingResult, HttpServletRequest request) {
        boolean success = false;
        model.addAttribute("captchaSiteKey", configurationUtils.getCaptchaSiteKey());
        if (bindingResult.hasErrors()) {
            return REGISTER_PAGE;
        }
        try {
            String responseToken = request.getParameter("g-recaptcha-response");
            captchaService.processResponse(responseToken);
            String registerToken = StringGeneratorUtils.getRandomString();
            String recoverToken = StringGeneratorUtils.getRandomString();
            String plainPassword = registerUser.getPassword();
            Role roleUser = roleService.findUserRole();
            List<Role> roles = new ArrayList<>();
            roles.add(roleUser);
            User user = new User();
            user.setRoles(roles);
            user.setEnabled(false);
            user.setExpired(false);
            user.setValidEmail(false);
            user.setCreationDate(new Date());
            user.setExpiredDate(DateUtils.addMonths(new Date(), 3));
            user.setRecoverPasswordToken(recoverToken);
            user.setRegisterToken(registerToken);
            user.setPassword(passwordEncoder.encode(plainPassword));
            user.setEmail(registerUser.getEmail());
            user.setRegisterIpAddress(RequestUtils.getIPAddress(request));
            user.setUsername(registerUser.getUsername());
            user.setFirstname(registerUser.getFirstName());
            user.setLastname(registerUser.getLastName());
            user = userService.saveAndFlush(user);

            HashMap<String, String> velocityContext = new HashMap<>();
            velocityContext.put("linkToValidate", configurationUtils.getSiteHomeURL() + "/validateEmail?registerToken=" + user.getRegisterToken() + "&email=" + user.getEmail());
            velocityContext.put("username", user.getUsername());
            velocityContext.put("password", plainPassword);
            emailUtils.sendEmail(configurationUtils.getEmail(), user.getEmail(), "Validate account", "mailValidateAccount.vm", velocityContext);
            success = true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            if (e instanceof ReCaptchaInvalidException) {
                model.addAttribute("invalidCaptcha", true);
            }
            if (e instanceof ReCaptchaUnavailableException) {
                model.addAttribute("unavailableCaptcha", true);
            }
        }
        model.addAttribute("success", success);
        return REGISTER_PAGE;
    }

    @RequestMapping(value = "/validateEmail", method = RequestMethod.GET)
    public String validateAccount(@RequestParam(value = "registerToken") String registerToken, @RequestParam(value = "email") String email) {
        User user = userService.findByRegisterTokenAndEmail(registerToken, email);
        if (user == null) {
            return "redirect:/login?validatedEmail=false";
        }
        user.setValidEmail(true);
        user.setRegisterToken(StringGeneratorUtils.getRandomString());
        userService.save(user);
        HashMap<String, String> velocityContext = new HashMap<>();
        velocityContext.put("loginURL", configurationUtils.getSiteHomeURL() + "/login");
        emailUtils.sendEmail(email, user.getEmail(), "Your email was validated successfully", "mailAcountValidated.vm", velocityContext);
        return "redirect:/login?validatedEmail=true";
    }
}
