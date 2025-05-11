package com.example.fitnessbackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@ActiveProfiles("test")
@Sql(statements = "CREATE SEQUENCE IF NOT EXISTS auth_token_seq START WITH 1", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class FitnessBackendApplicationTests {

    @Test
    void contextLoads() {
    }

}
