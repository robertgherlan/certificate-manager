package ro.certificate.manager.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.certificate.manager.entity.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    User findByUsername(String name);

    User findByRegisterToken(String registerToken);

    User findByEmail(String email);

    User findByUsernameOrEmail(String username, String email);

    User findByRecoverPasswordToken(String recoverToken);

    User findByRecoverPasswordTokenAndEmail(String recoverToken, String email);

    User findByRegisterTokenAndEmail(String registerToken, String email);

    Page<User> findByEnabledTrue(Pageable pageRequest);

    Page<User> findByValidEmailTrue(Pageable pageRequest);

    Page<User> findByEnabledFalseAndValidEmailTrue(Pageable pageRequest);

    Page<User> findByUsernameOrEmailOrFirstnameOrLastnameAllIgnoreCaseContaining(String userName, String userName1, String userName2, String userName3, Pageable pageRequest);

    List<User> findByUsernameOrEmailAllIgnoreCaseContaining(String username, String username2);
}
