package cmc.vn.ejbca.RA.dto.request;

public class RequestOfFindCertsDto {
    String userName;
    boolean onlyValid;

    public RequestOfFindCertsDto(String userName, boolean onlyValid) {
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
