package fr.dsc.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TrocApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrocApplication.class, args);
    }

}
