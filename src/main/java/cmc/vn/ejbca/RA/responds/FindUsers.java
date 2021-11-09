package cmc.vn.ejbca.RA.responds;

import java.util.List;

public class FindUsers {
    String search;
    List<Integer> usermatch;

    public FindUsers(String search, List<Integer> usermatch) {
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
