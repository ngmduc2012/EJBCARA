package cmc.vn.ejbca.RA.dto.respond;

public class CPsDto {
    String nameCP;
    int idCP;

    public CPsDto(String nameCP, int idCP) {
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
