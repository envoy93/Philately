package com.philately.view.converters;

import com.philately.mark.MarkParamsCache;
import com.philately.model.Country;
import com.philately.model.Paper;
import javafx.util.StringConverter;

/**
 * Created by kirill on 03.12.2015.
 */
public class PaperClassConverter extends StringConverter<Paper> {

    @Override
    public String toString(Paper myClassinstance) {
        return myClassinstance.getTitle();
    }

    @Override
    public Paper fromString(String string) {
        return MarkParamsCache.getInstance().getPaper(string);
    }
}