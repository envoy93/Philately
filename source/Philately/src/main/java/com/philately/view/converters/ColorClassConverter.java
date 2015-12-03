package com.philately.view.converters;

import com.philately.mark.MarkParamsCache;
import com.philately.model.Color;
import com.philately.model.Country;
import javafx.util.StringConverter;

/**
 * Created by kirill on 03.12.2015.
 */
public class ColorClassConverter extends StringConverter<Color> {

    @Override
    public String toString(Color myClassinstance) {
        return myClassinstance.getTitle();
    }

    @Override
    public Color fromString(String string) {
        return MarkParamsCache.getInstance().getColor(string);
    }
}