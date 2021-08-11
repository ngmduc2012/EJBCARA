package cmc.vn.ejbca.RA.api;

public class CPs {
    String nameCP;
    int idCP;

    public CPs(String nameCP, int idCP) {
        this.nameCP = nameCP;
        this.idCP = idCP;
    }

    public String getNameCP() {
        return nameCP;
    }

    public int getIdCP() {
        return idCP;
    }
}
