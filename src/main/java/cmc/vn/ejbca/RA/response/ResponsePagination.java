package cmc.vn.ejbca.RA.response;

//import lombok.Builder;
//import lombok.Getter;
//import lombok.Setter;
//
//@Getter
//@Setter
//@Builder
public class ResponsePagination<T> {
    private long totalItems;
    private long size;
    private T data;
    private int totalPages;
    private int currentPage;

    public ResponsePagination(long totalItems, long size, T data, int totalPages, int currentPage) {
        this.totalItems = totalItems;
        this.size = size;
        this.data = data;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}
