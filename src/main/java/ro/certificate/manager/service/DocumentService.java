package ro.certificate.manager.service;


import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ro.certificate.manager.entity.Document;
import ro.certificate.manager.entity.Keystore;
import ro.certificate.manager.entity.Signature;
import ro.certificate.manager.entity.User;
import ro.certificate.manager.repository.DocumentRepository;
import ro.certificate.manager.service.utils.*;
import ro.certificate.manager.utils.ErrorMessageBundle;
import ro.certificate.manager.utils.PaginationUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Date;

@Log4j2
@Service
@Transactional
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private KeystoreService keystoreService;

    @Autowired
    private DocumentUtils documentUtils;

    @Autowired
    private SignatureUtils signatureUtils;

    @Autowired
    private SignatureService signatureService;

    @Autowired
    private CertificateUtils certificateUtils;

    @Autowired
    private FolderUtils folderUtils;

    public Page<Document> findAll(Integer pageNumber, Integer perPage, String sortDirection, String sortBy) {
        PageRequest pageRequest = PaginationUtils.getPageRequest(pageNumber, perPage, sortDirection, sortBy);
        return documentRepository.findAll(pageRequest);
    }

    public Document saveAndFlush(Document document) {
        return documentRepository.saveAndFlush(document);
    }

    public Page<Document> findByUser(String userName, Integer pageNumber, Integer perPage, String sortDirection, String sortBy) {
        try {
            User user = userService.findByUsername(userName);
            PageRequest pageRequest = PaginationUtils.getPageRequest(pageNumber, perPage, sortDirection, sortBy);
            return documentRepository.findByUser(pageRequest, user);
        } catch (Exception e) {
            log.error(e);
        }

        return null;
    }

    public Page<Document> searchByNameAndUser(String query, String userName, Integer pageNumber, Integer perPage, String sortDirection, String sortBy) {
        try {
            User user = userService.findByUsername(userName);
            PageRequest pageRequest = PaginationUtils.getPageRequest(pageNumber, perPage, sortDirection, sortBy);
            return documentRepository.findByUserAndNameIgnoreCaseContaining(pageRequest, user, query);
        } catch (Exception e) {
            log.error(e);
        }

        return null;
    }

    public Page<Document> searchByName(String query, Integer pageNumber, Integer perPage, String sortDirection, String sortBy) {
        try {
            PageRequest pageRequest = PaginationUtils.getPageRequest(pageNumber, perPage, sortDirection, sortBy);
            return documentRepository.findByNameIgnoreCaseContaining(pageRequest, query);
        } catch (Exception e) {
            log.error(e);
        }

        return null;
    }

    public void signDocument(String userName, MultipartFile documentToSign, String keyStoreID, RedirectAttributes redirectAttributes) throws Exception {
        User user = userService.findByUsername(userName);
        Keystore keystore = keystoreService.findByUserAndCertificateID(user, keyStoreID);
        String userID = user.getId();
        String originalFileName = documentToSign.getOriginalFilename();
        String documentPath = documentUtils.saveDocumentOnDisk(documentToSign, userID);
        KeyStore keyStore = certificateUtils.getKeyStore(keystore, user);
        java.security.cert.Certificate certificate = CertificateUtils.extractCertificateFromKeystore(keyStore);
        Key key = certificateUtils.extractPrivateKey(keyStore, keystore.getPrivateKeyPassword());
        if (!(key instanceof PrivateKey)) {
            throw new NotFoundException(ErrorMessageBundle.CERTIFICATE_NOT_FOUND);
        }
        byte[] generatedSignature = CertificateUtils.signDocument(documentToSign.getBytes(), (PrivateKey) key, ((X509Certificate) certificate).getSigAlgName());
        String signatureFileName = signatureUtils.saveSignature(generatedSignature, originalFileName, userID, keyStoreID);

        Date currentDate = new Date();
        Document document = new Document();
        document.setCreationDate(currentDate);
        document.setKeystore(keystore);
        document.setName(originalFileName);
        document.setPath(documentPath);
        document.setUser(user);
        document = saveAndFlush(document);

        Signature signature = new Signature();
        signature.setCreationDate(currentDate);
        signature.setPath(signatureFileName);
        signature.setName(originalFileName + ".sig");
        signature.setDocument(document);
        signature.setKeystore(keystore);
        signature = signatureService.saveAndFlush(signature);

        document.setSignature(signature);
        saveAndFlush(document);

        redirectAttributes.addFlashAttribute("documentID", document.getId());
        redirectAttributes.addFlashAttribute("signatureID", signature.getId());
    }

    public Document findByDocumentIdAndSignature(String documentID, Signature signature) {
        if (ValidationUtils.validateUUID(documentID)) {
            Document document = documentRepository.findBySignatureAndId(signature, documentID);
            if (document == null) {
                throw new NotFoundException(ErrorMessageBundle.DOCUMENT_NOT_FOUND);
            }

            return document;
        }

        throw new NotFoundException(ErrorMessageBundle.DOCUMENT_NOT_FOUND);
    }

    public Document findByDocumentIdAndUser(String documentID, User user) {
        if (ValidationUtils.validateUUID(documentID)) {
            Document document = documentRepository.findByUserAndId(user, documentID);
            if (document == null) {
                throw new NotFoundException(ErrorMessageBundle.DOCUMENT_NOT_FOUND);
            }

            return document;
        }

        throw new NotFoundException(ErrorMessageBundle.DOCUMENT_NOT_FOUND);
    }

    public InputStream downloadDocument(String userId, String path) throws Exception {
        if (ValidationUtils.validateUUID(userId)) {
            File userDocumentsFolder = folderUtils.getDocumentsFolderFile(userId);
            File documentFile = new File(userDocumentsFolder, path);
            FileUtils.checkIfNotExist(documentFile);
            return new BufferedInputStream(new FileInputStream(documentFile));
        }
        throw new NotFoundException(ErrorMessageBundle.DOCUMENT_NOT_FOUND);
    }

}
