package ro.certificate.manager.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ro.certificate.manager.entity.User;
import ro.certificate.manager.exceptions.UserNotFoundException;
import ro.certificate.manager.repository.UserRepository;
import ro.certificate.manager.service.utils.ValidationUtils;
import ro.certificate.manager.utils.ErrorMessageBundle;
import ro.certificate.manager.utils.PaginationUtils;

@Service
@Transactional
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PaginationUtils paginationUtils;

	public void save(User user) {
		userRepository.saveAndFlush(user);
	}

	public List<User> findAll() throws Exception {
		List<User> allUsers = userRepository.findAll();
		if (allUsers == null || allUsers.isEmpty()) {
			throw new UserNotFoundException(ErrorMessageBundle.USER_NOT_FOUND);
		}
		return allUsers;
	}

	public User findOne(String id) throws Exception {
		if (ValidationUtils.validateUUID(id)) {
			User userFromDatabase = userRepository.getOne(id);
			if (userFromDatabase == null) {
				throw new UserNotFoundException(ErrorMessageBundle.USER_NOT_FOUND);
			}
			return userFromDatabase;
		}
		throw new UserNotFoundException(ErrorMessageBundle.USER_NOT_FOUND);
	}

	public User findByUsername(String username) throws Exception {
		User user = userRepository.findByUsername(username);
		if (user == null) {
			throw new UserNotFoundException(ErrorMessageBundle.USER_NOT_FOUND);
		}
		return user;
	}

	public Page<User> findAll(Integer pageNumber, Integer perPage, String sortDirection, String sortBy) {
		PageRequest pageRequest = paginationUtils.getPageRequest(pageNumber, perPage, sortDirection, sortBy);
		return findAll(pageRequest);
	}

	public Page<User> findAll(PageRequest pageRequest) {
		return userRepository.findAll(pageRequest);
	}

	public Page<User> searchUser(String query, Integer pageNumber, Integer perPage, String sortDirection,
			String sortBy) {
		Page<User> users = null;
		PageRequest pageRequest = paginationUtils.getPageRequest(pageNumber, perPage, sortDirection, sortBy);
		if (query != null && query.trim().length() > 0) {
			users = findByUsernameOrEmailOrFirstnameOrLastnameAllIgnoreCaseContaining(query.trim(), pageRequest);
		} else {
			users = findAll(pageRequest);
		}

		return users;
	}

	public User findByRegisterToken(String registerToken) {
		return userRepository.findByRegisterToken(registerToken);
	}

	public void deleteById(String id) {
		if (ValidationUtils.validateUUID(id)) {
			userRepository.delete(id);
		}
	}

	public User findByUsernameOrEmail(String username, String email) {
		return userRepository.findByUsernameOrEmail(username, email);
	}

	public User findByRecoverPaswordToken(String recoverToken) {
		return userRepository.findByRecoverPaswordToken(recoverToken);
	}

	public List<User> searchByUsernameOrEmail(String username) {
		return userRepository.findByUsernameOrEmailAllIgnoreCaseContaining(username, username);
	}

	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public Page<User> findByUsernameOrEmailOrFirstnameOrLastnameAllIgnoreCaseContaining(String query,
			Pageable pageRequest) {
		return userRepository.findByUsernameOrEmailOrFirstnameOrLastnameAllIgnoreCaseContaining(query, query, query,
				query, pageRequest);
	}

	public User findByRecoverPaswordTokenAndEmail(String recoverToken, String email) {
		return userRepository.findByRecoverPaswordTokenAndEmail(recoverToken, email);
	}

	public User findByRegisterTokenAndEmail(String registerToken, String email) {
		return userRepository.findByRegisterTokenAndEmail(registerToken, email);
	}

	public Page<User> findUsersInvalidatedEmail(Integer pageNumber, Integer perPage, String sortDirection,
			String sortby) {
		PageRequest pageRequest = paginationUtils.getPageRequest(pageNumber, perPage, sortDirection, sortby);
		return userRepository.findByValidEmailTrue(pageRequest);
	}

	public Page<User> findUsersRequests(Integer pageNumber, Integer perPage, String sortDirection, String sortBy) {
		PageRequest pageRequest = paginationUtils.getPageRequest(pageNumber, perPage, sortDirection, sortBy);
		return userRepository.findByEnabledFalseAndValidEmailTrue(pageRequest);
	}

	public Page<User> findAcceptedUsers(Integer pageNumber, Integer perPage, String sortDirection, String sortBy) {
		PageRequest pageRequest = paginationUtils.getPageRequest(pageNumber, perPage, sortDirection, sortBy);
		return userRepository.findByEnabledTrue(pageRequest);
	}

	public void disableUser(String id) {
		if (ValidationUtils.validateUUID(id)) {
			User user = userRepository.getOne(id);
			if (user != null && user.isEnabled()) {
				user.setEnabled(false);
				userRepository.save(user);
			}
		}
	}

	public void enableUser(String id) {
		if (ValidationUtils.validateUUID(id)) {
			User user = userRepository.getOne(id);
			if (user != null && !user.isEnabled()) {
				user.setEnabled(true);
				userRepository.save(user);
			}
		}
	}

	public User saveAndFlush(User user) {
		return userRepository.saveAndFlush(user);
	}
}
