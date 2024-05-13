package ru.shotin.spring.demo.project1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import ru.shotin.spring.demo.project1.currency.controller.CurrencyController;
import ru.shotin.spring.demo.project1.security.WebSecurityConfig;

@SpringBootApplication
//        (scanBasePackageClasses = {
//                WebSecurityConfig.class, CurrencyController.class
//        })
public class DemoProject1Application {

    public static void main(String[] args) {
        SpringApplication.run(DemoProject1Application.class, args);
    }

}
