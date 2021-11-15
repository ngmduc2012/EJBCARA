package cmc.vn.ejbca.RA.dto.request;

public class RequestOfGenerateKeysDto {
    String keySpec;
    String keyalgorithmRsa;

    public RequestOfGenerateKeysDto(String keySpec, String keyalgorithmRsa) {
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
