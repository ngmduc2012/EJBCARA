package cmc.vn.ejbca.RA.dto.request;

public class RequestOfCertificateRequestFromP10Dto {
    String keySpec;
    String keyalgorithmRsa;
    String signatureAlgorithm;
    String dn;
    String userName;
    String password;
    String hardTokenSN;
    String responseType;

    public RequestOfCertificateRequestFromP10Dto(String keySpec, String keyalgorithmRsa, String signatureAlgorithm, String dn, String userName, String password, String hardTokenSN, String responseType) {
        this.keySpec = keySpec;
        this.keyalgorithmRsa = keyalgorithmRsa;
        this.signatureAlgorithm = signatureAlgorithm;
        this.dn = dn;
        this.userName = userName;
        this.password = password;
        this.hardTokenSN = hardTokenSN;
        this.responseType = responseType;
    }

    public String getKeySpec() {
        return keySpec;
    }

    public String getKeyalgorithmRsa() {
        return keyalgorithmRsa;
    }

    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public String getDn() {
        return dn;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getHardTokenSN() {
        return hardTokenSN;
    }

    public String getResponseType() {
        return responseType;
    }
}
