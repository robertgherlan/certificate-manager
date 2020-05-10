package ro.certificate.manager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.certificate.manager.entity.Role;
import ro.certificate.manager.repository.RoleRepository;

import java.util.List;

@Service
@Transactional
public class RoleService {

	@Autowired
	private RoleRepository roleRepository;

	public Role save(Role role) {
		if (roleRepository.countByName(role.getName()) != 0) {
			return null;
		}

		return roleRepository.saveAndFlush(role);
	}

	public List<Role> findAll() {
		return roleRepository.findAll();
	}

	public Role findByName(String name) {
		return roleRepository.findByName(name);
	}

	public Role findUserRole() {
		Role userRole = roleRepository.findByName("USER");
		if (userRole == null) {
			userRole = save(new Role("USER"));
		}
		return userRole;
	}

	public Role findAdminRole() {
		Role adminRole = roleRepository.findByName("ADMIN");
		if (adminRole == null) {
			adminRole = save(new Role("ADMIN"));
		}
		return adminRole;
	}
}
