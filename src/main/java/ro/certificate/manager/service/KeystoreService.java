package ro.certificate.manager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.certificate.manager.entity.Keystore;
import ro.certificate.manager.entity.User;
import ro.certificate.manager.repository.KeystoreRepository;
import ro.certificate.manager.service.utils.ValidationUtils;
import ro.certificate.manager.utils.ErrorMessageBundle;


import java.util.List;

@Service
@Transactional
public class KeystoreService {

	@Autowired
	private KeystoreRepository keystoreRepository;

	public List<Keystore> findAll() {
		return keystoreRepository.findAll();
	}

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
			Keystore keystore = keystoreRepository.findOne(id);
			if (keystore == null) {
				throw new NotFoundException(ErrorMessageBundle.CERTIFICATE_NOT_FOUND);
			}

			return keystore;
		}
		throw new NotFoundException(ErrorMessageBundle.CERTIFICATE_NOT_FOUND);
	}
}
