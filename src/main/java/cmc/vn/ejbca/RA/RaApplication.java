package cmc.vn.ejbca.RA;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@RestController
public class RaApplication {
    public static void main(String[] args) {
        SpringApplication.run(RaApplication.class, args);
    }
}
