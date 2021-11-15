package cmc.vn.ejbca.RA.dto.respond;

public class AvailableCADto {
    String name;
    int id;

    public AvailableCADto(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
