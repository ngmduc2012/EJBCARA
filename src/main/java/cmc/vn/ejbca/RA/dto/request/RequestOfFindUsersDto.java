package cmc.vn.ejbca.RA.dto.request;

import java.util.List;

public class RequestOfFindUsersDto {
    String search;
    List<Integer> usermatch;

    public RequestOfFindUsersDto(String search, List<Integer> usermatch) {
        this.search = search;
        this.usermatch = usermatch;
    }

    public String getSearch() {
        return search;
    }

    public List<Integer> getUsermatch() {
        return usermatch;
    }
}
