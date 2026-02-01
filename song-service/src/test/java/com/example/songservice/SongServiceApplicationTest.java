package com.example.songservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"eureka.client.enabled=false"})
class SongServiceApplicationTest {

    @Test
    void contextLoads() {
    }
}
