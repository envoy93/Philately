package com.philately.mark;

import com.philately.model.*;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import org.hibernate.classic.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kirill on 05.11.2015.
 */
public class MarkParamsCache {
    private HashMap<String, Country> countries = new HashMap<>();
    private HashMap<String, Color> colors = new HashMap<>();
    private HashMap<String, Paper> papers = new HashMap<>();
    private HashMap<String, Currency> currency = new HashMap<>();

    private static MarkParamsCache instance;

    public static synchronized MarkParamsCache getInstance() {
        if (instance == null) {
            instance = new MarkParamsCache();
        }
        return instance;
    }

    private MarkParamsCache() {
        reCache();
    }

    public ArrayList<Country> getCountries(){
        return new ArrayList(countries.values());
    }

    public Country getCountry(String key){
        return countries.get(key);
    }

    public ArrayList<Color> getColors(){
        return new ArrayList(colors.values());
    }

    public Color getColor(String key){
        return colors.get(key);
    }

    public ArrayList<Paper> getPapers(){
        return new ArrayList(papers.values());
    }

    public Paper getPaper(String key){
        return papers.get(key);
    }

    public ArrayList<Currency> getCurrency(){
        return new ArrayList(currency.values());
    }

    public Currency getCurrency(String key){
        return currency.get(key);
    }

    public void reCache(){
        countries.clear();
        for (Country country : (List<Country>) HibernateUtil.getSession().createCriteria(Country.class).list()) {
            countries.put(country.getTitle(), country);
        }

        colors.clear();
        for (Color color : (List<Color>) HibernateUtil.getSession().createCriteria(Color.class).list()) {
            colors.put(color.getTitle(), color);
        }

        papers.clear();
        for (Paper paper : (List<Paper>) HibernateUtil.getSession().createCriteria(Paper.class).list()) {
            papers.put(paper.getTitle(), paper);
        }

        currency.clear();
        for (Currency c : (List<Currency>) HibernateUtil.getSession().createCriteria(Currency.class).list()) {
            currency.put(c.getTitle(), c);
        }
    }
}
