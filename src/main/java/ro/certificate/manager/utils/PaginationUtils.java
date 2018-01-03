package ro.certificate.manager.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
public class PaginationUtils {

	@Autowired
	private ConfigurationUtils configurationUtils;

	public PageRequest getPageRequest(Integer pageNumber, Integer perPage, String sortDirection, String sortBy) {
		Sort sort = null;
		if (pageNumber == null || pageNumber < 0) {
			pageNumber = 0;
		}
		if (perPage == null || perPage < 1) {
			perPage = configurationUtils.getDefaultUsersPerPage();
		}
		if (sortBy == null || sortBy.trim().length() == 0) {
			sortBy = "ID";
		}
		if ("ASC".equalsIgnoreCase(sortDirection)) {
			sort = new Sort(Direction.ASC, sortBy);
		} else if ("DESC".equalsIgnoreCase(sortDirection)) {
			sort = new Sort(Direction.DESC, sortBy);
		}

		return new PageRequest(pageNumber, perPage, sort);
	}
}
