package cmc.vn.ejbca.RA.api;

import java.util.List;

public class EndEntityList {
    String name;
    int id;
    List<CAs> cAsList;
    List<CPs> cPsList;

    public EndEntityList(String name, int id, List<CAs> cAsList, List<CPs> cPsList) {
        this.name = name;
        this.id = id;
        this.cAsList = cAsList;
        this.cPsList = cPsList;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public List<CAs> getcAsList() {
        return cAsList;
    }

    public List<CPs> getcPsList() {
        return cPsList;
    }
}
