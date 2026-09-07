package com.br.eprontuario.authcommon.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class User {
    private UUID id;
    private String username;
    private String email;
    private Set<String> roles;

    public boolean hasRole(String role) {
        return roles.contains(role);
    }
}
