package ro.certificate.manager.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

public class PaginationUtils {

    private PaginationUtils() {
    }

    public static PageRequest getPageRequest(Integer pageNumber, Integer perPage, String sortDirection, String sortBy) {
        Sort sort = null;
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = 0;
        }
        if (perPage == null || perPage < 1) {
            perPage = 10;
        }
        if (sortBy == null || sortBy.trim().isEmpty()) {
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
