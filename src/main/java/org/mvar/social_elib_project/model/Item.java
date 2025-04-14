package org.mvar.social_elib_project.model;

import lombok.Getter;

import java.util.Date;

public class Item {
    @Getter
    private int id;
    private String name;
    private String author;
    private String description;
    private String category;
    private Date date;
    private String image;
    private String pdfLink;
    private User user;
    private ExpertComment expertComment;
}
