package org.mvar.social_elib_project.model;

import lombok.Getter;

import java.util.Date;

public class ExpertComment {
    @Getter
    private int id;
    private String text;
    private Date date;
    private Item item;
    private User user;
}
