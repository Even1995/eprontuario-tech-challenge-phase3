package com.br.historico.service.config;

import graphql.scalars.coercing.Coercing;
import graphql.scalars.coercing.CoercingSerializeException;
import graphql.scalars.coercing.CoercingParseValueException;
import graphql.scalars.coercing.CoercingParseLiteralException;
import graphql.language.StringValue;
import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class GraphQLConfig {
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    @Bean
    public GraphQLScalarType dateTimeScalar() {
        return GraphQLScalarType.newScalar()
            .name("DateTime")
            .description("Custom DateTime scalar type")
            .coercing(new Coercing<LocalDateTime, String>() {
                
                @Override
                public String serialize(Object dataFetcherResult) throws CoercingSerializeException {
                    if (dataFetcherResult instanceof LocalDateTime) {
                        return formatter.format((LocalDateTime) dataFetcherResult);
                    }
                    throw new CoercingSerializeException("Unable to serialize " + dataFetcherResult + " as DateTime");
                }
                
                @Override
                public LocalDateTime parseValue(Object input) throws CoercingParseValueException {
                    if (input instanceof String) {
                        try {
                            return LocalDateTime.parse((String) input, formatter);
                        } catch (Exception e) {
                            throw new CoercingParseValueException("Unable to parse '" + input + "' as DateTime", e);
                        }
                    }
                    throw new CoercingParseValueException("Unable to parse '" + input + "' as DateTime");
                }
                
                @Override
                public LocalDateTime parseLiteral(Object input) throws CoercingParseLiteralException {
                    if (input instanceof StringValue) {
                        try {
                            return LocalDateTime.parse(((StringValue) input).getValue(), formatter);
                        } catch (Exception e) {
                            throw new CoercingParseLiteralException("Unable to parse '" + input + "' as DateTime", e);
                        }
                    }
                    throw new CoercingParseLiteralException("Unable to parse '" + input + "' as DateTime");
                }
            })
            .build();
    }
}
