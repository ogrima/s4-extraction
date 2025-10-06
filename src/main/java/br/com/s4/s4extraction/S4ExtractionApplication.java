package br.com.s4.s4extraction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class S4ExtractionApplication {

    public static void main(String[] args) {
        SpringApplication.run(S4ExtractionApplication.class, args);
    }

}
