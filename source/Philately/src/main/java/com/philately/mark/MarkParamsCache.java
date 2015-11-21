package com.philately.mark;

import com.philately.model.Country;
import com.philately.model.HibernateUtil;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import org.hibernate.classic.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kirill on 05.11.2015.
 */
public class MarkParamsCache {
    private HashMap<Integer, Country> countries = new HashMap<>();

    private static MarkParamsCache instance;

    public static synchronized MarkParamsCache getInstance() {
        if (instance == null) {
            instance = new MarkParamsCache();
        }
        return instance;
    }

    private MarkParamsCache() {


        for (Country country : (List<Country>) HibernateUtil.getSession().createCriteria(Country.class).list()) {
            countries.put(country.getId(), country);
        }


    }

    public ArrayList<Country> getCountries(){
        return new ArrayList(countries.values());
    }

    public Country getCountry(Integer key){
        return countries.get(key);
    }
}
