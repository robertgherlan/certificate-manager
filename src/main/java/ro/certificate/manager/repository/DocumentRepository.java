package ro.certificate.manager.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ro.certificate.manager.entity.Document;
import ro.certificate.manager.entity.Signature;
import ro.certificate.manager.entity.User;

@Repository
public interface DocumentRepository extends JpaRepository<Document, String> {

	Page<Document> findByUser(Pageable pageable, User user);

	Document findByUserAndId(User user, String documentID);

	Page<Document> findByUserAndNameIgnoreCaseContaining(Pageable pageRequest, User user, String query);

	Page<Document> findByNameIgnoreCaseContaining(Pageable pageRequest, String query);

	Document findBySignatureAndId(Signature signature, String documentID);

}
