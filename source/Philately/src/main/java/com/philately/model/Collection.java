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

    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "mark_id", referencedColumnName = "id")
    private Mark mark;

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

    public Mark getMark() {
        return mark;
    }

    public void setMark(Mark mark) {
        this.mark = mark;
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
