package cmc.vn.ejbca.RA.dto.respond;

public class CAsDto {
    String nameCA;
    int idCA;

    public CAsDto(String nameCA, int idCA) {
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
