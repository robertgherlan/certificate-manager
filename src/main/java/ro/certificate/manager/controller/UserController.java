package ro.certificate.manager.controller;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ro.certificate.manager.entity.User;

@Controller
public class UserController extends BaseController {

	private static final String REDIRECT_USERS_PAGE = "redirect:/users";

	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public String usersPage(Model model, @RequestParam(required = false, value = "pageNumber") Integer pageNumber,
			@RequestParam(required = false, value = "perPage") Integer perPage,
			@RequestParam(required = false, value = "sortDirection") String sortDirection,
			@RequestParam(required = false, value = "sortBy") String sortBy) {
		Page<User> users = userService.findAll(pageNumber, perPage, sortDirection, sortBy);
		return getUsersPage(users, model);
	}

	@RequestMapping(value = "/users/accepted", method = RequestMethod.GET)
	public String usersAcceptedPage(Model model,
			@RequestParam(required = false, value = "pageNumber") Integer pageNumber,
			@RequestParam(required = false, value = "perPage") Integer perPage,
			@RequestParam(required = false, value = "sortDirection") String sortDirection,
			@RequestParam(required = false, value = "sortBy") String sortBy) {
		Page<User> users = userService.findAcceptedUsers(pageNumber, perPage, sortDirection, sortBy);
		return getUsersPage(users, model);
	}

	@RequestMapping(value = "/users/requests", method = RequestMethod.GET)
	public String usersRequestsPage(Model model,
			@RequestParam(required = false, value = "pageNumber") Integer pageNumber,
			@RequestParam(required = false, value = "perPage") Integer perPage,
			@RequestParam(required = false, value = "sortDirection") String sortDirection,
			@RequestParam(required = false, value = "sortBy") String sortBy) {
		Page<User> users = userService.findUsersRequests(pageNumber, perPage, sortDirection, sortBy);
		return getUsersPage(users, model);
	}

	@RequestMapping(value = "/users/invalidated_emails", method = RequestMethod.GET)
	public String usersUnvalidatedEmailPage(Model model,
			@RequestParam(required = false, value = "pageNumber") Integer pageNumber,
			@RequestParam(required = false, value = "perPage") Integer perPage,
			@RequestParam(required = false, value = "sortDirection") String sortDirection,
			@RequestParam(required = false, value = "sortBy") String sortBy) {
		Page<User> users = userService.findUsersInvalidatedEmail(pageNumber, perPage, sortDirection, sortBy);
		return getUsersPage(users, model);
	}

	@RequestMapping(value = "/users/search", method = RequestMethod.GET)
	public String searchUsersPage(Model model, @RequestParam(required = true, value = "query") String query,
			@RequestParam(required = false, value = "pageNumber") Integer pageNumber,
			@RequestParam(required = false, value = "perPage") Integer perPage,
			@RequestParam(required = false, value = "sortDirection") String sortDirection,
			@RequestParam(required = false, value = "sortBy") String sortBy) {
		Page<User> users = userService.searchUser(query, pageNumber, perPage, sortDirection, sortBy);
		return getUsersPage(users, model);
	}

	@RequestMapping(value = "/users/disable_user/{id}", method = RequestMethod.POST)
	public String disableUser(@PathVariable("id") String id) {
		userService.disableUser(id);
		return REDIRECT_USERS_PAGE;
	}

	@RequestMapping(value = "/users/enable_user/{id}", method = RequestMethod.POST)
	public String enableUser(@PathVariable("id") String id) {
		userService.enableUser(id);
		return REDIRECT_USERS_PAGE;
	}

	@RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
	public String deleteAccount(@PathVariable("id") String id) {
		userService.deleteById(id);
		return REDIRECT_USERS_PAGE;
	}

	@RequestMapping(value = "/users/resend_validation_email/{id}", method = RequestMethod.POST)
	public String resendValidationEmailUser(@PathVariable("id") String id) {
		try {
			User user = userService.findOne(id);
			if (user != null) {
				String email = user.getEmail();
				String registerToken = user.getRegisterToken();
				if (email != null && registerToken != null) {

				}
			}
		} catch (Exception e) {

		}
		return REDIRECT_USERS_PAGE;
	}

	/**
	 * Get users page.
	 * 
	 * @param users
	 *            Users which will be added in page.
	 * @param model
	 *            The model.
	 * @return the users page path.
	 */
	private String getUsersPage(Page<User> users, Model model) {
		if (users != null && !users.getContent().isEmpty()) {
			model.addAttribute("exist", true);
			model.addAttribute("users", users);
		} else {
			model.addAttribute("exist", false);
		}
		return "/users";
	}
}
