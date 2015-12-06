package com.philately.view.converters;

import com.philately.mark.MarkParamsCache;
import com.philately.model.Color;
import com.philately.model.Currency;
import javafx.util.StringConverter;

/**
 * Created by kirill on 03.12.2015.
 */
public class CurrencyClassConverter extends StringConverter<Currency> {

    @Override
    public String toString(Currency myClassinstance) {
        return myClassinstance.getTitle();
    }

    @Override
    public Currency fromString(String string) {
        return MarkParamsCache.getInstance().getCurrency(string);
    }
}