package ro.certificate.manager.wrapper;

public class UserSearchModel {

	private String search;

	private Integer pageSize;

	private String sortDirection;

	private String sortBy;

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getSortDirection() {
		return sortDirection;
	}

	public void setSortDirection(String sortDirection) {
		this.sortDirection = sortDirection;
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

}
