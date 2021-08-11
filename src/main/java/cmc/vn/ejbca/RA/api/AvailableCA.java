package cmc.vn.ejbca.RA.api;

public class AvailableCA {
    String name;
    int id;

    public AvailableCA(String name, int id) {
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
