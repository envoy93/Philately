package com.philately.view.converters;

import com.philately.mark.MarkParamsCache;
import com.philately.model.Country;
import javafx.util.StringConverter;

/**
 * Created by kirill on 03.12.2015.
 */
public class CountryClassConverter extends StringConverter<Country> {

    @Override
    public String toString(Country myClassinstance) {
        return myClassinstance.getTitle();
    }

    @Override
    public Country fromString(String string) {
        return MarkParamsCache.getInstance().getCountry(string);
    }
}