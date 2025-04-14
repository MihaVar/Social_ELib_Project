package org.mvar.social_elib_project.model;

import lombok.Getter;

public class User {
    @Getter
    private String id;
    private String name;
    private String email;
    private String password;
    private Role role;
}
