package ro.certificate.manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.certificate.manager.entity.Keystore;
import ro.certificate.manager.entity.User;

import java.util.List;

@Repository
public interface KeystoreRepository extends JpaRepository<Keystore, String> {

	List<Keystore> findByUser(User user);

	List<Keystore> findByUserAndCertificateSubjectContainingIgnoreCase(User user, String query);

	Keystore findByUserAndId(User user, String keyStoreID);

}
