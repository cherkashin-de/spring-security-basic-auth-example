package com.security.auth.model.security;

import java.security.Principal;

public class AuthPrincipal implements Principal {

    private String login;

    public AuthPrincipal(String login) {
        this.login = login;
    }

    @Override
    public String getName() {
        return this.login;
    }
}
