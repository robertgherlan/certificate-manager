package ro.certificate.manager.controller;

import java.io.InputStream;
import java.security.Principal;

import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ro.certificate.manager.entity.Document;
import ro.certificate.manager.entity.Signature;
import ro.certificate.manager.entity.User;

@Controller
public class DocumentController extends BaseController {

	@RequestMapping(value = "/documents", method = RequestMethod.GET)
	public String allDocuments(@RequestParam(required = false, value = "pageNumber") Integer pageNumber,
			@RequestParam(required = false, value = "perPage") Integer perPage,
			@RequestParam(required = false, value = "sortDirection") String sortDirection,
			@RequestParam(required = false, value = "sortBy") String sortBy, Model model) {

		Page<Document> documents = documentService.findAll(pageNumber, perPage, sortDirection, sortBy);
		return getDocumentsPage(documents, model);
	}

	@RequestMapping(value = "/documents/search", method = RequestMethod.GET)
	public String allDocumentsSearch(@RequestParam(required = false, value = "pageNumber") Integer pageNumber,
			@RequestParam(required = false, value = "perPage") Integer perPage,
			@RequestParam(required = false, value = "sortDirection") String sortDirection,
			@RequestParam(required = false, value = "sortBy") String sortBy,
			@RequestParam(required = false, value = "query") String query, Model model) {

		Page<Document> documents = null;
		if (query != null && query.trim().length() > 0) {
			documents = documentService.searchByName(query, pageNumber, perPage, sortDirection, sortBy);
		} else {
			documents = documentService.findAll(pageNumber, perPage, sortDirection, sortBy);
		}

		return getDocumentsPage(documents, model);
	}

	@RequestMapping(value = "/my_documents", method = RequestMethod.GET)
	public String myDocuments(Principal principal,
			@RequestParam(required = false, value = "pageNumber") Integer pageNumber,
			@RequestParam(required = false, value = "perPage") Integer perPage,
			@RequestParam(required = false, value = "sortDirection") String sortDirection,
			@RequestParam(required = false, value = "sortBy") String sortBy, Model model) {

		Page<Document> myDocuments = documentService.findByUser(principal.getName(), pageNumber, perPage, sortDirection,
				sortBy);
		return getDocumentsPage(myDocuments, model);

	}

	@RequestMapping(value = "/my_documents/search", method = RequestMethod.GET)
	public String myDocumentsSearch(Principal principal,
			@RequestParam(required = false, value = "pageNumber") Integer pageNumber,
			@RequestParam(required = false, value = "perPage") Integer perPage,
			@RequestParam(required = false, value = "sortDirection") String sortDirection,
			@RequestParam(required = false, value = "sortBy") String sortBy,
			@RequestParam(required = false, value = "query") String query, Model model) {
		Page<Document> myDocuments = null;
		if (query != null && query.trim().length() > 0) {
			myDocuments = documentService.searchByNameAndUser(query.trim(), principal.getName(), pageNumber, perPage,
					sortDirection, sortBy);
		} else {
			myDocuments = documentService.findByUser(principal.getName(), pageNumber, perPage, sortDirection, sortBy);
		}

		return getDocumentsPage(myDocuments, model);
	}

	@RequestMapping(value = "/documents/download_signature", method = RequestMethod.POST)
	public void downloadSignature(@RequestParam(required = true, value = "documentID") String documentID,
			@RequestParam(required = true, value = "signatureID") String signatureID, HttpServletResponse response)
			throws Exception {

		Signature signature = signatureService.findOne(signatureID);
		Document document = documentService.findByDocumentIdAndSignature(documentID, signature);
		User user = document.getUser();
		if (user == null) {
			throw new NotFoundException("No signature was found.");
		}
		InputStream inputStream = signatureService.downloadSignature(user.getId(), signature.getPath());
		fileUtils.downloadFile(response, inputStream, signature.getName());
	}

	@RequestMapping(value = "/documents/download_document", method = RequestMethod.POST)
	public void downloadDocument(@RequestParam(required = true, value = "documentID") String documentID,
			@RequestParam(required = true, value = "userID") String userID, HttpServletResponse response)
			throws Exception {

		User user = userService.findOne(userID);
		Document document = documentService.findByDocumentIdAndUser(documentID, user);
		InputStream inputStream = documentService.downloadDocument(user.getId(), document.getPath());
		fileUtils.downloadFile(response, inputStream, document.getName());
	}

	private String getDocumentsPage(Page<Document> documents, Model model) {
		if (documents != null && !documents.getContent().isEmpty()) {
			model.addAttribute("exist", true);
			model.addAttribute("documents", documents);
		} else {
			model.addAttribute("exist", false);
		}
		return "/documents";
	}
}
