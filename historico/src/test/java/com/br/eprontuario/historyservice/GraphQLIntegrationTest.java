package com.br.eprontuario.historyservice;

import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.test.context.TestPropertySource;

@GraphQlTest
@AutoConfigureDataJpa
@TestPropertySource(locations = "classpath:application-test.yml")
public class GraphQLIntegrationTest {
    
    // GraphQL integration tests will go here
    // Test queries, mutations, and subscriptions
    // Verify resolver behavior with real database queries
}
