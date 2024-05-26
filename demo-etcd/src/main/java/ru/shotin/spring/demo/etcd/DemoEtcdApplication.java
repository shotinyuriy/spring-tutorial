package ru.shotin.spring.demo.etcd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DemoEtcdApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoEtcdApplication.class, args);
	}

}
