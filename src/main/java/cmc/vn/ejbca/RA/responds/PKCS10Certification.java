package cmc.vn.ejbca.RA.responds;

public class PKCS10Certification {

    String keySpec;
    String keyalgorithmRsa;
    String signatureAlgorithm;
    String dn;

    public PKCS10Certification(String keySpec, String keyalgorithmRsa, String signatureAlgorithm, String dn) {
        this.keySpec = keySpec;
        this.keyalgorithmRsa = keyalgorithmRsa;
        this.signatureAlgorithm = signatureAlgorithm;
        this.dn = dn;
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
}
