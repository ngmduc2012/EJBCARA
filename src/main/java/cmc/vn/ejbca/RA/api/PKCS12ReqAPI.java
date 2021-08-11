package cmc.vn.ejbca.RA.api;

public class PKCS12ReqAPI {

    String userName;
    String password;
    String hardTokenSN;
    String keyspec;
    String keyalg;

    public PKCS12ReqAPI(String userName, String password, String hardTokenSN, String keyspec, String keyalg) {
        this.userName = userName;
        this.password = password;
        this.hardTokenSN = hardTokenSN;
        this.keyspec = keyspec;
        this.keyalg = keyalg;
    }

    public String getUsername() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getHardTokenSN() {
        return hardTokenSN;
    }

    public String getKeyspec() {
        return keyspec;
    }

    public String getKeyalg() {
        return keyalg;
    }
}
