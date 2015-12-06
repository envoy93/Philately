package com.philately.model;

import com.philately.Utility;
import javafx.scene.image.Image;

import javax.persistence.*;
import java.io.File;

/**
 * Created by kirill on 04.11.2015.
 */

@Entity
@Table(name = "Mark")
public class Mark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "country", referencedColumnName = "id")
    private Country country;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "color", referencedColumnName = "id")
    private Color color;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "paper", referencedColumnName = "id")
    private Paper paper;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "currency", referencedColumnName = "id")
    private Currency currency;

    private int year;

    private boolean cancellation;

    private String theme;

    private String series;

    private String separation;

    private int edition;

    private String size;

    private Double price;

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

    public void setCountry(Country country) {
        this.country = country;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Paper getPaper() {
        return paper;
    }

    public void setPaper(Paper paper) {
        this.paper = paper;
    }

    public boolean isCancellation() {
        return cancellation;
    }

    public void setCancellation(boolean cancellation) {
        this.cancellation = cancellation;
    }

    public int getEdition() {
        return edition;
    }

    public void setEdition(int edition) {
        this.edition = edition;
    }

    public String getSeparation() {
        return separation;
    }

    public void setSeparation(String separation) {
        this.separation = separation;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Image getImage() {
        if (Utility.getInstance().getFileImage(String.valueOf(getId())).exists()) {
            return new Image(Utility.getInstance().getFullPathToImage(String.valueOf(getId())));
        } else {
            return new Image(Utility.getInstance().getFullPathToImage(null));
        }
    }
}
