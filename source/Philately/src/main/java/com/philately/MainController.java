package com.philately;

import com.philately.mark.MarkParamsCache;
import com.philately.model.Country;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.util.StringConverter;

/**
 * Created by kirill on 22.10.2015.
 */
public class MainController {

    @FXML
    ChoiceBox countryChoiceBox;

    //after fxml has been loaded
    @FXML
    private void initialize() {
        countryChoiceBox.setItems(FXCollections.observableList(MarkParamsCache.getInstance().getCountries()));
        countryChoiceBox.getSelectionModel().selectFirst();
        countryChoiceBox.setConverter(new CountryClassConverter());

    }

    class CountryClassConverter extends StringConverter<Country> {

        @Override
        public String toString(Country myClassinstance) {
            return myClassinstance.getTitle();
        }

        @Override
        public Country fromString(String string) {
            return null; //MarkParamsCache.getInstance().getCountry(string);
        }

    }
}
