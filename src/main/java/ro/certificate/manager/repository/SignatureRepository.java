package ro.certificate.manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ro.certificate.manager.entity.Signature;

@Repository
public interface SignatureRepository extends JpaRepository<Signature, String> {

}
