package ro.certificate.manager.wrapper;

import lombok.Data;

@Data
public class SearchModel {

    private String query;

    private int pageNumber;

    private int perPage;

    private String sortDirection;

    private String sortBy;
}