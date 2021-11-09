package cmc.vn.ejbca.RA.responds;

public class Keys {
    String publicKey;
    String privateKey;

    public Keys(String publicKey, String privateKey) {
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
