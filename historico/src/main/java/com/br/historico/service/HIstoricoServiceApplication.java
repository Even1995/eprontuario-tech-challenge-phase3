package com.br.historico.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.br.historico.service"})
public class HIstoricoServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HIstoricoServiceApplication.class, args);
    }
}
