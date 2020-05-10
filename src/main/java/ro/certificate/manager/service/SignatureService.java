package ro.certificate.manager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ro.certificate.manager.entity.Signature;
import ro.certificate.manager.entity.User;
import ro.certificate.manager.repository.SignatureRepository;
import ro.certificate.manager.service.utils.CertificateUtils;
import ro.certificate.manager.service.utils.SignatureUtils;
import ro.certificate.manager.service.utils.ValidationUtils;
import ro.certificate.manager.utils.ErrorMessageBundle;
import ro.certificate.manager.utils.PaginationUtils;

import java.io.InputStream;

@Service
@Transactional
public class SignatureService {

    @Autowired
    private UserService userService;

    @Autowired
    private SignatureRepository signatureRepository;

    @Autowired
    private SignatureUtils signatureUtils;

    @Autowired
    private CertificateUtils certificateUtils;

    public Page<Signature> findAll(Integer pageNumber, Integer perPage, String sortDirection, String sortBy) {
        PageRequest pageRequest = PaginationUtils.getPageRequest(pageNumber, perPage, sortDirection, sortBy);
        return signatureRepository.findAll(pageRequest);
    }

    public Signature saveAndFlush(Signature signature) {
        return signatureRepository.saveAndFlush(signature);
    }

    public Signature findOne(String signatureID) {
        if (ValidationUtils.validateUUID(signatureID)) {
            Signature signature = signatureRepository.getOne(signatureID);
            if (signature == null) {
                throw new NotFoundException(ErrorMessageBundle.SIGNATURE_NOT_FOUND);
            }

            return signature;
        }
        throw new NotFoundException(ErrorMessageBundle.SIGNATURE_NOT_FOUND);
    }

    public InputStream downloadSignature(String userId, String signatureFileName) throws Exception {
        if (ValidationUtils.validateUUID(userId)) {
            return signatureUtils.downloadSignature(userId, signatureFileName);
        }
        throw new NotFoundException(ErrorMessageBundle.SIGNATURE_NOT_FOUND);
    }

    public User verifySignature(MultipartFile signedDocument, MultipartFile signature) throws Exception {
        return certificateUtils.verifySignature(signedDocument, signature);
    }

    public boolean verifySignatureByUser(MultipartFile signedDocument, String userID) {
        boolean success = false;
        try {
            User user = userService.findOne(userID);
            success = certificateUtils.verifySignatureByUser(signedDocument, user);
        } catch (Exception e) {
            // Stay silent.
        }
        return success;
    }
}
