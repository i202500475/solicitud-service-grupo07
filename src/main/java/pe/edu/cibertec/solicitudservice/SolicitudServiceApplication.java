package pe.edu.cibertec.solicitudservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class SolicitudServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SolicitudServiceApplication.class, args);
    }
}
