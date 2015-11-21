package com.philately.mark;

import com.philately.model.Country;
import com.philately.model.HibernateUtil;
import com.philately.model.Mark;

import java.util.HashMap;
import java.util.List;

/**
 * Created by kirill on 05.11.2015.
 */
public class MarksFilter {
    private static MarksFilter instance;

    private MarksFilter(){

    }

    public static synchronized MarksFilter getInstance() {
        if (instance == null) {
            instance = new MarksFilter();
        }
        return instance;
    }

    public static List<Mark> getMarks(){
        return HibernateUtil.getSession().createCriteria(Mark.class).list();
    }
}
