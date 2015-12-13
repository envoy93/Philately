package com.philately.model;


import javax.persistence.*;

/**
 * Created by kirill on 04.11.2015.
 */
@Entity
@Table(name = "COUNTRY")
public class Country  implements java.io.Serializable, ParamWithTitle{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;

    public Country() {
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
