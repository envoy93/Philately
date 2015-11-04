package com.philately.model;

import javax.persistence.*;

/**
 * Created by kirill on 04.11.2015.
 */

@Entity
@Table(name = "Mark")
public class Mark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Country country;

    public Mark() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Country getCountry() {
        return country;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id")
    public void setCountry(Country country) {
        this.country = country;
    }
}
