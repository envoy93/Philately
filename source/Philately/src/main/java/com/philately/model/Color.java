package com.philately.model;

import javax.persistence.*;

/**
 * Created by kirill on 29.11.2015.
 */
@Entity
@Table(name = "COLOR")
public class Color implements java.io.Serializable, ParamWithTitle{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;

    public Color() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
