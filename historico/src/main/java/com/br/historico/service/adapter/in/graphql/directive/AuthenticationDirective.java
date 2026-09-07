package com.br.historico.service.adapter.in.graphql.directive;

import graphql.schema.GraphQLFieldDefinition;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationDirective {
    
    public void validate(GraphQLFieldDefinition field) throws AccessDeniedException {
        if (field.getDirective("authenticated") != null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            
            if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
                throw new AccessDeniedException("Access denied: user must be authenticated");
            }
        }
    }
}
