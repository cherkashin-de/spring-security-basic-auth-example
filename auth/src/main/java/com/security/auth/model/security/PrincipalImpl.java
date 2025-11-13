package com.security.auth.model.security;

import java.security.Principal;

public class PrincipalImpl implements Principal {

    private final String login;

    public PrincipalImpl(String login) {
        this.login = login;
    }

    @Override
    public String getName() {
        return this.login;
    }
}
