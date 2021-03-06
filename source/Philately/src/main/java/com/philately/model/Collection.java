package com.philately.model;

import javax.persistence.*;

/**
 * Created by kirill on 02.12.2015.
 */

@Entity
@Table(name = "COLLECTION")
public class Collection implements java.io.Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "INFO")
    private String info;

    @Column(name = "amount")
    private int amount;

    public Collection() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
