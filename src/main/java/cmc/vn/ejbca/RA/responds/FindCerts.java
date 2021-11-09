package cmc.vn.ejbca.RA.responds;

public class FindCerts {
    String userName;
    boolean onlyValid;

    public FindCerts(String userName, boolean onlyValid) {
        this.userName = userName;
        this.onlyValid = onlyValid;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isOnlyValid() {
        return onlyValid;
    }
}
