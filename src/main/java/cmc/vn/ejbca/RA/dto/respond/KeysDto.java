package cmc.vn.ejbca.RA.dto.respond;

public class KeysDto {
    String publicKey;
    String privateKey;

    public KeysDto(String publicKey, String privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }


}
