package com.philately;

import com.philately.mark.MarkParamsCache;
import com.philately.mark.MarksFilter;
import com.philately.model.Country;
import com.philately.model.HibernateUtil;
import com.philately.model.Mark;
import com.philately.view.MarkListCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * Created by kirill on 22.10.2015.
 */
public class MainController {

    @FXML
    ChoiceBox countryChoiceBox;

    @FXML
    ListView marksListView;

    @FXML
    ImageView markImage;

    // Reference to the main application.
    private MainApp mainApp;

    //after fxml has been loaded
    @FXML
    private void initialize() {
        countryChoiceBox.setItems(FXCollections.observableList(MarkParamsCache.getInstance().getCountries()));
        countryChoiceBox.getSelectionModel().selectFirst();
        countryChoiceBox.setConverter(new CountryClassConverter());


        ObservableList list = FXCollections.observableList(MarksFilter.getMarks());
        list.addAll(MarksFilter.getMarks());
        list.addAll(MarksFilter.getMarks());
        list.addAll(MarksFilter.getMarks());
        list.addAll(MarksFilter.getMarks());
        list.addAll(MarksFilter.getMarks());
        list.addAll(MarksFilter.getMarks());
        list.addAll(MarksFilter.getMarks());
        list.addAll(MarksFilter.getMarks());
        list.addAll(MarksFilter.getMarks());
        list.addAll(MarksFilter.getMarks());
        list.addAll(MarksFilter.getMarks());
        list.addAll(MarksFilter.getMarks());
        list.addAll(MarksFilter.getMarks());

        marksListView.setItems(list);
        marksListView.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                return new MarkListCell();
            }
        });

      /*  marksListView.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                // if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
                System.out.println("----------" + ((Mark) event.getSource()).getId());
                // }
                // event.consume();
            }
        });*/

        marksListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //TODO add information about mark
                markImage.setImage(new Image(Utility.getInstance().getFullPathToImage(null)));
            }
        });


    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }


    @FXML
    private void handleNewMark() {
        Mark tempMark = new Mark();
        boolean okClicked = mainApp.showMarkEditDialog(tempMark);
        if (okClicked) {
            //@TODO create mark
        }
    }

    /**
     * Called when the user clicks the edit button. Opens a dialog to edit
     * details for the selected person.
     */
    @FXML
    private void handleEditMark() {

        Mark selectedMark = (Mark) marksListView.getSelectionModel().getSelectedItem();
        if (selectedMark != null) {
            boolean okClicked = mainApp.showMarkEditDialog(selectedMark);
            if (okClicked) {
                //TODO save edit mark
            }

        } else {
            // Nothing selected.
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Mark Selected");
            alert.setContentText("Please select a mark in the table.");

            alert.showAndWait();
        }
    }

    @FXML
    private void handleDeleteMark() {
        Mark selectedMark = (Mark) marksListView.getSelectionModel().getSelectedItem();
        //TODO question + delete
    }

    class CountryClassConverter extends StringConverter<Country> {

        @Override
        public String toString(Country myClassinstance) {
            return myClassinstance.getTitle();
        }

        @Override
        public Country fromString(String string) {
            return null;
        }
    }
}
