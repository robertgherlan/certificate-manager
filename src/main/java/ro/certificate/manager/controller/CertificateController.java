package ro.certificate.manager.controller;

import java.io.InputStream;
import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ro.certificate.manager.entity.Keystore;
import ro.certificate.manager.entity.User;
import ro.certificate.manager.exceptions.InternalServerError;
import ro.certificate.manager.utils.ErrorMessageBundle;
import ro.certificate.manager.wrapper.Certificate;
import ro.certificate.manager.wrapper.CertificateDetails;
import ro.certificate.manager.wrapper.ImportCertificate;

/**
 * Handles requests for the application home page.
 */
@Controller
public class CertificateController extends BaseController {

	private static final Logger logger = Logger.getLogger(CertificateController.class);

	@RequestMapping(value = "/certificates", method = RequestMethod.GET)
	public String certificates(Model model, Principal principal,
			@RequestParam(value = "query", required = false) String query) {
		try {
			User user = userService.findByUsername(principal.getName());
			List<Keystore> keystores = null;
			if (query != null && query.trim().length() > 0) {
				keystores = keystoreService.findByUserAndCertificateSubjectContainingIgnoreCase(user, query);
			} else {
				keystores = keystoreService.findByUser(user);
			}
			if (keystores != null && !keystores.isEmpty()) {
				model.addAttribute("found", true);
				model.addAttribute("keystores", keystores);
			} else {
				model.addAttribute("found", false);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			model.addAttribute("error", ErrorMessageBundle.CANNOT_PROCESS_REQUEST + e.getMessage());
		}
		return "/certificates";
	}

	@RequestMapping(value = "/generate_certificate", method = RequestMethod.GET)
	public String generate_certificate(Model model) {
		model.addAttribute("certificate", new Certificate());

		return "/generate_certificate";
	}

	@RequestMapping(value = "/generate_certificate", method = RequestMethod.POST)
	public String generate_certificatePOST(Model model, @Valid @ModelAttribute("certificate") Certificate certificate,
			BindingResult bindingResult, Principal principal) {
		if (bindingResult.hasErrors()) {
			return "/generate_certificate";
		}
		try {
			User user = userService.findByUsername(principal.getName());
			certificateGeneratorUtils.generateCertificate(certificate, user);
			model.addAttribute("success", true);
		} catch (Exception e) {
			logger.error(e);
			model.addAttribute("error", ErrorMessageBundle.CANNOT_PROCESS_REQUEST + e.getMessage());
		}
		return "/generate_certificate";
	}

	@RequestMapping(value = "/delete_certificate", method = RequestMethod.POST)
	public String delete_certificatePOST(Model model, @RequestParam(value = "id", required = true) String id,
			Principal principal) {
		boolean deleted = false;
		try {
			User user = userService.findByUsername(principal.getName());
			certificateGeneratorUtils.deleteCertificate(user, id);
			deleted = true;
		} catch (Exception e) {
			logger.error(e);
		}
		model.addAttribute("deleted", deleted);
		return "redirect:" + certificates(model, principal, null);
	}

	@RequestMapping(value = "/import_certificate", method = RequestMethod.GET)
	public String import_certificate(Model model) {
		model.addAttribute("importCertificate", new ImportCertificate());

		return "/import_certificate";
	}

	@RequestMapping(value = "/import_certificate", method = RequestMethod.POST)
	public String import_certificatePOST(Model model,
			@Valid @ModelAttribute("importCertificate") ImportCertificate importCertificate,
			BindingResult bindingResult, Principal principal) {
		if (bindingResult.hasErrors()) {
			return "/import_certificate";
		}
		try {
			User user = userService.findByUsername(principal.getName());
			certificateGeneratorUtils.importCertificate(importCertificate.getCertificate(),
					importCertificate.getPrivateKey(), null, null, user);
			model.addAttribute("success", true);
		} catch (Exception e) {
			logger.error(e);
			model.addAttribute("error", ErrorMessageBundle.CANNOT_PROCESS_REQUEST + e.getMessage());
		}

		return "/import_certificate";
	}

	@RequestMapping(value = "/upload_certificate", method = RequestMethod.GET)
	public String upload_certificate(Model model) {
		return "/upload_certificate";
	}

	@RequestMapping(value = "/upload_certificate", method = RequestMethod.POST)
	public String upload_certificatePOST(Principal principal, @RequestParam("certificate") MultipartFile certificate,
			@RequestParam("privateKey") MultipartFile privateKey, RedirectAttributes redirectAttributes) {
		try {
			User user = userService.findByUsername(principal.getName());
			certificateGeneratorUtils.uploadCertificate(certificate, privateKey, user);
			redirectAttributes.addFlashAttribute("success", true);
		} catch (Exception e) {
			logger.error(e);
			redirectAttributes.addFlashAttribute("error", ErrorMessageBundle.CANNOT_PROCESS_REQUEST + e.getMessage());
		}

		return "redirect:/upload_certificate";
	}

	@RequestMapping(value = "/certificate_details/{id}", method = RequestMethod.GET)
	public String certificate_details(Model model, Principal principal, @PathVariable("id") String certificateID) {
		try {
			User user = userService.findByUsername(principal.getName());
			Keystore foundedKeystore = keystoreService.findByUserAndCertificateID(user, certificateID);
			if (foundedKeystore != null) {
				List<CertificateDetails> certificatesDetails = certificateGeneratorUtils
						.getCertificatesInfo(foundedKeystore, user);
				if (!certificatesDetails.isEmpty()) {
					model.addAttribute("certificatesDetails", certificatesDetails);
				} else {
					model.addAttribute("error", ErrorMessageBundle.CERTIFICATE_NOT_FOUND);
				}
			} else {
				model.addAttribute("error", ErrorMessageBundle.CERTIFICATE_NOT_FOUND);
			}

		} catch (Exception e) {
			logger.error(e);
			model.addAttribute("error", ErrorMessageBundle.CANNOT_PROCESS_REQUEST + e.getMessage());
		}

		return "/certificate_details";
	}

	@RequestMapping(value = "/generate_csr/{id}", method = RequestMethod.POST)
	public void generate_csr(Principal principal, HttpServletResponse response,
			@PathVariable("id") String certificateID) {
		try {
			User user = userService.findByUsername(principal.getName());
			Keystore findedKeystore = keystoreService.findByUserAndCertificateID(user, certificateID);
			if (findedKeystore != null) {
				InputStream inputStream = certificateGeneratorUtils.generateCSR(findedKeystore, user);
				fileUtils.downloadFile(response, inputStream, findedKeystore.getCertificateSubject() + ".csr");
			}
		} catch (Exception e) {
			logger.error(e);
			throw new InternalServerError(e.getMessage());
		}
	}

	@RequestMapping(value = "/export_privateKey/{id}", method = RequestMethod.POST)
	public void export_privateKey(Principal principal, HttpServletResponse response,
			@PathVariable("id") String certificateID, @RequestParam(value = "asPEM", required = false) Boolean asPEM) {
		try {
			User user = userService.findByUsername(principal.getName());
			Keystore findedKeystore = keystoreService.findByUserAndCertificateID(user, certificateID);
			if (findedKeystore != null) {
				InputStream inputStream = certificateGeneratorUtils.export_privateKey(findedKeystore, user, asPEM);
				fileUtils.downloadFile(response, inputStream, findedKeystore.getCertificateSubject() + ".key");
			}
		} catch (Exception e) {
			logger.error(e);
			throw new InternalServerError(e.getMessage());
		}
	}

	@RequestMapping(value = "/export_certificate/{id}", method = RequestMethod.POST)
	public void export_certificate(Principal principal, HttpServletResponse response,
			@PathVariable("id") String certificateID) {
		try {
			User user = userService.findByUsername(principal.getName());
			Keystore findedKeystore = keystoreService.findByUserAndCertificateID(user, certificateID);
			if (findedKeystore != null) {
				InputStream inputStream = certificateGeneratorUtils.exportCertificate(findedKeystore, user);
				fileUtils.downloadFile(response, inputStream, findedKeystore.getCertificateSubject() + ".cer");
			}
		} catch (Exception e) {
			logger.error(e);
			throw new InternalServerError(e.getMessage());
		}
	}
}
