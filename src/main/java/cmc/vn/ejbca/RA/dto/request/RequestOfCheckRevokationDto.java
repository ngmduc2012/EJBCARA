package cmc.vn.ejbca.RA.dto.request;

public class RequestOfCheckRevokationDto {

    String userName;
    boolean onlyValid;
    int idCert;

    public RequestOfCheckRevokationDto(String userName, boolean onlyValid, int idCert) {
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
