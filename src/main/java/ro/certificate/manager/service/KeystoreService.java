package ro.certificate.manager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.certificate.manager.entity.Keystore;
import ro.certificate.manager.entity.User;
import ro.certificate.manager.repository.KeystoreRepository;
import ro.certificate.manager.service.utils.ValidationUtils;
import ro.certificate.manager.utils.ErrorMessageBundle;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class KeystoreService {

    private final KeystoreRepository keystoreRepository;

    public Keystore save(Keystore keystore) {
        return keystoreRepository.saveAndFlush(keystore);
    }

    public List<Keystore> findByUser(User user) {
        return keystoreRepository.findByUser(user);
    }

    public Keystore findByUserAndCertificateID(User user, String keyStoreID) {
        if (ValidationUtils.validateUUID(keyStoreID)) {
            Keystore keystore = keystoreRepository.findByUserAndId(user, keyStoreID);
            if (keystore == null) {
                throw new NotFoundException(ErrorMessageBundle.CERTIFICATE_NOT_FOUND);
            }
            return keystore;
        }

        throw new NotFoundException(ErrorMessageBundle.CERTIFICATE_NOT_FOUND);
    }

    public List<Keystore> findByUserAndCertificateSubjectContainingIgnoreCase(User user, String query) {
        return keystoreRepository.findByUserAndCertificateSubjectContainingIgnoreCase(user, query);
    }

    public Keystore findById(String id) {
        if (ValidationUtils.validateUUID(id)) {
            Optional<Keystore> keystore = keystoreRepository.findById(id);
            return keystore.orElseThrow(() -> new NotFoundException(ErrorMessageBundle.CERTIFICATE_NOT_FOUND));
        }
        throw new NotFoundException(ErrorMessageBundle.CERTIFICATE_NOT_FOUND);
    }
}
