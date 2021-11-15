package cmc.vn.ejbca.RA.dto.request;

import javax.persistence.Entity;

@Entity
public class RequestOfUserAPIDto {

    private String userName;
    private String password;
    private boolean clearPwd;
    private String subjectDN;
    private String CaName;
    private String tokenType;
    private int status;
    private String email;
    private String subjectAltName;
    private String endEntityProfileName;
    private String certificateProfileName;
    private String startTime;

    public RequestOfUserAPIDto(String userName, String password, boolean clearPwd, String subjectDN, String CaName, String tokenType, int status, String email, String subjectAltName, String endEntityProfileName, String certificateProfileName, String startTime) {
        this.userName = userName;
        this.password = password;
        this.clearPwd = clearPwd;
        this.subjectDN = subjectDN;
        this.CaName = CaName;
        this.tokenType = tokenType;
        this.status = status;
        this.email = email;
        this.subjectAltName = subjectAltName;
        this.endEntityProfileName = endEntityProfileName;
        this.certificateProfileName = certificateProfileName;
        this.startTime = startTime;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public boolean isClearPwd() {
        return clearPwd;
    }

    public String getSubjectDN() {
        return subjectDN;
    }

    public String getCaName() {
        return CaName;
    }

    public String getTokenType() {
        return tokenType;
    }

    public int getStatus() {
        return status;
    }

    public String getEmail() {
        return email;
    }

    public String getSubjectAltName() {
        return subjectAltName;
    }

    public String getEndEntityProfileName() {
        return endEntityProfileName;
    }

    public String getCertificateProfileName() {
        return certificateProfileName;
    }

    public String getStartTime() {
        return startTime;
    }
}
