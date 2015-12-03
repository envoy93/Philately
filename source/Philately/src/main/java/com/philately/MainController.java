package com.philately;

import com.philately.mark.MarkParamsCache;
import com.philately.mark.MarksFilter;
import com.philately.model.Country;
import com.philately.model.HibernateUtil;
import com.philately.model.Mark;
import com.philately.view.MarkListCell;
import com.philately.view.converters.ColorClassConverter;
import com.philately.view.converters.CountryClassConverter;
import com.philately.view.converters.PaperClassConverter;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.hibernate.classic.Session;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

/**
 * Created by kirill on 22.10.2015.
 */
public class MainController {

    @FXML
    ChoiceBox countryChoiceBox;
    @FXML
    ChoiceBox colorChoiceBox;
    @FXML
    ChoiceBox paperChoiceBox;

    @FXML
    ListView marksListView;

    @FXML
    ImageView markImage;

    @FXML
    ToolBar markToolbar;

    @FXML
    VBox markVBoxPanel;


    // Reference to the main application.
    private MainApp mainApp;

    //after fxml has been loaded
    @FXML
    private void initialize() {
        setMark(null);
        countryChoiceBox.setItems(FXCollections.observableList(MarkParamsCache.getInstance().getCountries()));
        countryChoiceBox.getSelectionModel().selectFirst();
        countryChoiceBox.setConverter(new CountryClassConverter());

        colorChoiceBox.setItems(FXCollections.observableList(MarkParamsCache.getInstance().getColors()));
        colorChoiceBox.getSelectionModel().selectFirst();
        colorChoiceBox.setConverter(new ColorClassConverter());

        paperChoiceBox.setItems(FXCollections.observableList(MarkParamsCache.getInstance().getPapers()));
        paperChoiceBox.getSelectionModel().selectFirst();
        paperChoiceBox.setConverter(new PaperClassConverter());


        ObservableList list = FXCollections.observableList(MarksFilter.getMarks());
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
                setMark((Mark) marksListView.getSelectionModel().getSelectedItem());
            }
        });


    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }


    @FXML
    private void handleNewMark() {
        boolean okClicked = mainApp.showMarkEditDialog(null);
        if (okClicked) {
            //@TODO research
            ObservableList list = FXCollections.observableList(MarksFilter.getMarks());
            marksListView.setItems(list);
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
                //TODO update viewed mark
                ObservableList list = FXCollections.observableList(MarksFilter.getMarks());
                marksListView.setItems(list);
                setMark(selectedMark);
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

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Удалить марку из справочника?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {

            Session session = HibernateUtil.getSession();
            session.beginTransaction();
            session.delete(selectedMark);
            session.getTransaction().commit();
            //TODO hide view + research
            ObservableList list = FXCollections.observableList(MarksFilter.getMarks());
            marksListView.setItems(list);
            setMark(null);
        }
    }

    @FXML
    private Label countryField;
    @FXML
    private Label yearField;
    @FXML
    private Label cancellationField;
    @FXML
    private Label themeField;
    @FXML
    private Label seriesField;
    @FXML
    private Label priceField;
    @FXML
    private Label editionField;
    @FXML
    private Label separationField;
    @FXML
    private Label paperField;
    @FXML
    private Label sizeField;
    @FXML
    private Label colorField;

    private void setMark(Mark mark){
        if (mark == null) { //hide
            markToolbar.setVisible(false);
            markVBoxPanel.setVisible(false);
        } else {
            markToolbar.setVisible(true);
            markVBoxPanel.setVisible(true);
            //TODO image
            markImage.setImage(new Image(Utility.getInstance().getFullPathToImage(null)));
            countryField.setText(mark.getCountry().getTitle());
            yearField.setText(String.valueOf(mark.getYear()));
            cancellationField.setText(mark.isCancellation()? "Да": "Нет");
            themeField.setText(mark.getTheme());
            seriesField.setText(mark.getSeries());
            priceField.setText(mark.getPrice());
            editionField.setText(String.valueOf(mark.getEdition()));
            separationField.setText(mark.getSeparation());
            paperField.setText(mark.getPaper().getTitle());
            sizeField.setText(mark.getSize());
            colorField.setText(mark.getColor().getTitle());


        }
    }
}
