package com.example.e2etests;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
//import org.springframework.cloud.openfeign.EnableFeignClients;

//@EnableDiscoveryClient
//@EnableFeignClients
@SpringBootApplication
public class E2eTestsApplication {

    public static void main(String[] args) {
        SpringApplication.run(E2eTestsApplication.class, args);
    }
}
