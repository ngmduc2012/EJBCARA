package cmc.vn.ejbca.RA.dto.respond;

public class TextRespondHttpClientDto {
    String respond;

    public String getRespond() {
        return respond;
    }

    public void setRespond(String respond) {
        this.respond = respond;
    }

    public TextRespondHttpClientDto(String respond) {
        this.respond = respond;
    }
}
