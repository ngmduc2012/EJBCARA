package cmc.vn.ejbca.RA.responds;

public class CheckRevokation {

    String userName;
    boolean onlyValid;
    int idCert;

    public CheckRevokation(String userName, boolean onlyValid, int idCert) {
        this.userName = userName;
        this.onlyValid = onlyValid;
        this.idCert = idCert;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isOnlyValid() {
        return onlyValid;
    }

    public int getIdCert() {
        return idCert;
    }
}
