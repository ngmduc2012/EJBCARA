package cmc.vn.ejbca.RA.responds;

public class GenerateKeys {
    String keySpec;
    String keyalgorithmRsa;

    public GenerateKeys(String keySpec, String keyalgorithmRsa) {
        this.keySpec = keySpec;
        this.keyalgorithmRsa = keyalgorithmRsa;
    }

    public String getKeySpec() {
        return keySpec;
    }

    public String getKeyalgorithmRsa() {
        return keyalgorithmRsa;
    }
}
