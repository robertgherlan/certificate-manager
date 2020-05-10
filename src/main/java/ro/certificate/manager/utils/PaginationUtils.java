package ro.certificate.manager.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import ro.certificate.manager.wrapper.SearchModel;

public class PaginationUtils {

    private PaginationUtils() {
    }

    public static PageRequest getPageRequest(SearchModel searchModel) {
        return getPageRequest(searchModel.getPageNumber(), searchModel.getPerPage(), searchModel.getSortDirection(), searchModel.getSortBy());
    }

    public static PageRequest getPageRequest(int pageNumber, int perPage, String sortDirection, String sortBy) {
        Sort sort = null;
        if (pageNumber < 0) {
            pageNumber = 0;
        }
        if (perPage < 1) {
            perPage = 10;
        }
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "ID";
        }
        if ("ASC".equalsIgnoreCase(sortDirection)) {
            sort = Sort.by(Direction.ASC, sortBy);
        } else if ("DESC".equalsIgnoreCase(sortDirection)) {
            sort = Sort.by(Direction.DESC, sortBy);
        }

        return PageRequest.of(pageNumber, perPage, sort);
    }
}
