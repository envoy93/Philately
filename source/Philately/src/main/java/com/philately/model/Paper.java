package com.philately.model;

import javax.persistence.*;

/**
 * Created by kirill on 02.12.2015.
 */
@Entity
@Table(name = "Paper")
public class Paper implements java.io.Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;

    public Paper() {
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
