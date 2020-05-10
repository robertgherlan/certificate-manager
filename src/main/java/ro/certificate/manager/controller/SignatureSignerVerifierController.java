package ro.certificate.manager.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ro.certificate.manager.entity.Keystore;
import ro.certificate.manager.entity.User;
import ro.certificate.manager.utils.ErrorMessageBundle;

import java.security.Principal;
import java.util.List;

@Controller
public class SignatureSignerVerifierController extends BaseController {

    private static final Logger logger = Logger.getLogger(SignatureSignerVerifierController.class);

    @RequestMapping(value = "/sign_document", method = RequestMethod.GET)
    public String sign_document(Model model, Principal principal) {
        try {
            User user = userService.findByUsername(principal.getName());
            List<Keystore> keystores = keystoreService.findByUser(user);
            model.addAttribute("keyStores", keystores);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "/sign_document";
    }

    @RequestMapping(value = "/sign_document", method = RequestMethod.POST)
    public String sign_documentPOST(Principal principal, @RequestParam(value = "documentToSign", required = false) MultipartFile documentToSign, @RequestParam("keyStoreID") String keyStoreID, RedirectAttributes redirectAttributes) {
        boolean success = false;
        try {
            if (documentToSign == null || documentToSign.isEmpty()) {
                throw new Exception("You must provide a document.");
            }
            documentService.signDocument(principal.getName(), documentToSign, keyStoreID, redirectAttributes);
            success = true;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        redirectAttributes.addFlashAttribute("success", success);
        return "redirect:/sign_document";
    }

    @RequestMapping(value = "/signature_verification_by_signature", method = RequestMethod.GET)
    public String signature_verification() {
        return "/signature_verification_by_signature";
    }

    @RequestMapping(value = "/signature_verification_by_signature", method = RequestMethod.POST)
    public String signature_verificationPOST(@RequestParam("signedDocument") MultipartFile signedDocument, @RequestParam("signature") MultipartFile signature, RedirectAttributes redirectAttributes) {
        boolean success = false;
        if ((signature == null || signature.isEmpty()) && (signedDocument == null || signedDocument.isEmpty())) {
            redirectAttributes.addFlashAttribute("error", ErrorMessageBundle.PROVIDE_BOTH_FILES);
        } else {
            try {
                User user = signatureService.verifySignature(signedDocument, signature);
                if (user != null) {
                    success = true;
                    redirectAttributes.addFlashAttribute("user", user);
                }
            } catch (Exception e) {
                logger.error(e);
                redirectAttributes.addFlashAttribute("error", e.getMessage());
            }
        }
        redirectAttributes.addFlashAttribute("success", success);
        return "redirect:/signature_verification_by_signature";
    }

    @RequestMapping(value = "/signature_verification_by_user", method = RequestMethod.GET)
    public String signature_verification_by_user(Model model) {
        try {
            model.addAttribute("users", userService.findAll());
        } catch (Exception e) {
            // Stay silent.
        }
        return "/signature_verification_by_user";
    }

    @RequestMapping(value = "/signature_verification_by_user", method = RequestMethod.POST)
    public String signature_verification_by_user_POST(@RequestParam("signedDocument") MultipartFile signedDocument, @RequestParam("user") String userID, RedirectAttributes redirectAttributes) {
        boolean success = signatureService.verifySignatureByUser(signedDocument, userID);
        redirectAttributes.addFlashAttribute("success", success);
        return "redirect:/signature_verification_by_user";
    }
}
