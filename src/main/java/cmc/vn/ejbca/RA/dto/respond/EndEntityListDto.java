package cmc.vn.ejbca.RA.dto.respond;

import java.util.List;

public class EndEntityListDto {
    String name;
    int id;
    List<CAsDto> cAsList;
    List<CPsDto> cPsList;

    public EndEntityListDto(String name, int id, List<CAsDto> cAsList, List<CPsDto> cPsList) {
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

    public List<CAsDto> getcAsList() {
        return cAsList;
    }

    public List<CPsDto> getcPsList() {
        return cPsList;
    }
}
