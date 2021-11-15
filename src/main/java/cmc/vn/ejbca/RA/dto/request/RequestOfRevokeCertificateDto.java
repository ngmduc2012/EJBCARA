package cmc.vn.ejbca.RA.dto.request;

public class RequestOfRevokeCertificateDto {

    String userName;
    boolean onlyValid;
    int idCert;
    int reason;

    public RequestOfRevokeCertificateDto(String userName, boolean onlyValid, int idCert, int reason) {
        this.userName = userName;
        this.onlyValid = onlyValid;
        this.idCert = idCert;
        this.reason = reason;
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

    public int getReason() {
        return reason;
    }
}
