package cmc.vn.ejbca.RA.api;

public class CAs {
    String nameCA;
    int idCA;

    public CAs(String nameCA, int idCA) {
        this.nameCA = nameCA;
        this.idCA = idCA;
    }


    public String getNameCA() {
        return nameCA;
    }

    public int getIdCA() {
        return idCA;
    }
}
