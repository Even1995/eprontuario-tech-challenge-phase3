package com.br.hospital.historyservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yml")
public class HistoryServiceApplicationTests {

    @Test
    public void contextLoads() {
        assertTrue(true);
    }
}
