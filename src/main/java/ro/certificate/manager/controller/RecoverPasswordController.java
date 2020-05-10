package ro.certificate.manager.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ro.certificate.manager.entity.User;
import ro.certificate.manager.exceptions.ReCaptchaInvalidException;
import ro.certificate.manager.exceptions.ReCaptchaUnavailableException;
import ro.certificate.manager.utils.StringGeneratorUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@Controller
public class RecoverPasswordController extends BaseController {

    private static final Logger logger = Logger.getLogger(RecoverPasswordController.class);

    @RequestMapping(value = "/recover", method = RequestMethod.GET)
    public String recoverGET(Model model) {
        model.addAttribute("captchaSiteKey", configurationUtils.getCaptchaSiteKey());
        return "/recover";
    }

    @RequestMapping(value = "/recover", method = RequestMethod.POST)
    public String recoverPOST(@RequestParam(value = "username") String username, HttpServletRequest request, Model model) {
        boolean success = true;
        model.addAttribute("captchaSiteKey", configurationUtils.getCaptchaSiteKey());
        try {
            String responseToken = request.getParameter("g-recaptcha-response");
            captchaService.processResponse(responseToken);
            User user = userService.findByUsernameOrEmail(username, username);
            if (user != null) {
                user.setEnabled(false);
                userService.save(user);
                HashMap<String, String> velocityContext = new HashMap<>();
                velocityContext.put("linkToRecover", configurationUtils.getSiteHomeURL() + "/recoverPassword?recoverToken=" + user.getRecoverPasswordToken() + "&email=" + user.getEmail());
                emailUtils.sendEmail(configurationUtils.getEmail(), user.getEmail(), "Recover password", "mailRecoverPassword.vm", velocityContext);
            } else {
                model.addAttribute("userNotFound", true);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            if (e instanceof ReCaptchaInvalidException) {
                model.addAttribute("invalidCaptcha", true);
            }
            if (e instanceof ReCaptchaUnavailableException) {
                model.addAttribute("unavailableCaptcha", true);
            }
            success = false;
        }
        model.addAttribute("recover", success);

        return "/recover";
    }

    @RequestMapping(value = "/recoverPassword", method = RequestMethod.GET)
    public String recoverPasswordPOST(@RequestParam(value = "recoverToken") String recoverToken, @RequestParam(value = "email") String email) {
        if (recoverToken == null || email == null) {
            return "redirect:/login?recover=false";
        }
        try {
            User user = userService.findByRecoverPasswordTokenAndEmail(recoverToken, email);
            if (user != null) {
                return "/recoverPassword";
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return "redirect:/login?recover=false";
    }

    @RequestMapping(value = "/recoverPassword", method = RequestMethod.POST)
    public String recoverPasswordPOST(@RequestParam(value = "recoverToken") String recoverToken, @RequestParam(value = "newPassword") String newPassword, @RequestParam(value = "retypeNewPassword") String retypeNewPassword) {
        try {
            if (newPassword.equals(retypeNewPassword)) {
                User user = userService.findByRecoverPasswordToken(recoverToken);
                if (user != null) {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    user.setRecoverPasswordToken(StringGeneratorUtils.getRandomString());
                    user.setEnabled(true);
                    userService.save(user);
                    HashMap<String, String> velocityContext = new HashMap<>();
                    velocityContext.put("username", user.getUsername());
                    velocityContext.put("password", newPassword);
                    velocityContext.put("loginURL", configurationUtils.getSiteHomeURL() + "/login");
                    emailUtils.sendEmail(configurationUtils.getEmail(), user.getEmail(), "Password was changed successfully.", "passwordChanged.vm", velocityContext);

                    return "redirect:/login?recoverPassword=true";
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return "redirect:/recoverPassword?error=true&recoverToken=" + recoverToken;
    }
}
