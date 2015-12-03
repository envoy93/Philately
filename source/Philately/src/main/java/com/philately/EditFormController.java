package com.philately;

import com.philately.mark.MarkParamsCache;
import com.philately.model.*;
import com.philately.view.converters.ColorClassConverter;
import com.philately.view.converters.CountryClassConverter;
import com.philately.view.converters.PaperClassConverter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.hibernate.classic.Session;


/**
 * Created by kirill on 21.11.2015.
 */
public class EditFormController {

    private Stage dialogStage;
    private Mark mark;
    private boolean okClicked = false;

    @FXML
    private ChoiceBox countryField;
    @FXML
    private TextField yearField;
    @FXML
    private CheckBox cancellationField;
    @FXML
    private TextField themeField;
    @FXML
    private TextField seriesField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField editionField;
    @FXML
    private TextField separationField;
    @FXML
    private ChoiceBox paperField;
    @FXML
    private TextField sizeField;
    @FXML
    private ChoiceBox colorField;

    @FXML
    private void initialize() {
        countryField.setItems(FXCollections.observableList(MarkParamsCache.getInstance().getCountries()));
        countryField.getSelectionModel().selectFirst();
        countryField.setConverter(new CountryClassConverter());

        colorField.setItems(FXCollections.observableList(MarkParamsCache.getInstance().getColors()));
        colorField.getSelectionModel().selectFirst();
        colorField.setConverter(new ColorClassConverter());

        paperField.setItems(FXCollections.observableList(MarkParamsCache.getInstance().getPapers()));
        paperField.getSelectionModel().selectFirst();
        paperField.setConverter(new PaperClassConverter());

        editionField.lengthProperty().addListener(new NumberChangedListener(editionField));

        yearField.lengthProperty().addListener(new YearChangedListener(yearField));
    }

    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMark(Mark mark) {
        this.mark = mark;
        //@TODO set params to fields
        if (mark == null) {  //creation dialog
            countryField.getSelectionModel().selectFirst();
            colorField.getSelectionModel().selectFirst();
            paperField.getSelectionModel().selectFirst();
        } else {
            countryField.getSelectionModel().select(mark.getCountry());
            paperField.getSelectionModel().select(mark.getPaper());
            colorField.getSelectionModel().select(mark.getColor());
            yearField.setText(String.valueOf(mark.getYear()));
            cancellationField.setSelected(mark.isCancellation());
            themeField.setText(mark.getTheme());
            seriesField.setText(mark.getSeries());
            priceField.setText(mark.getPrice());
            editionField.setText(String.valueOf(mark.getEdition()));
            separationField.setText(mark.getSeparation());
            sizeField.setText(mark.getSize());
        }
    }

    /**
     * Returns true if the user clicked OK, false otherwise.
     *
     * @return
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Called when the user clicks ok.
     */
    @FXML
    private void handleOk() {
        if (isInputValid()) {
            //TODO set from fields to mark
            if (mark == null) {
                mark = new Mark();
            }
            mark.setCountry((Country) countryField.getSelectionModel().getSelectedItem());
            mark.setPaper((Paper) paperField.getSelectionModel().getSelectedItem());
            mark.setColor((Color) colorField.getSelectionModel().getSelectedItem());
            mark.setYear(Integer.parseInt(yearField.getText()));
            mark.setCancellation(cancellationField.isSelected());
            mark.setTheme(themeField.getText());
            mark.setSeries(seriesField.getText());
            mark.setPrice(priceField.getText());
            mark.setEdition(Integer.parseInt(editionField.getText())); //TODO
            mark.setSeparation(separationField.getText());
            mark.setSize(sizeField.getText());

            Session session = HibernateUtil.getSession();

            session.beginTransaction();
            session.save(mark);
            session.getTransaction().commit();

            okClicked = true;
            dialogStage.close();
        }
    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    /**
     * Validates the user input in the text fields.
     *
     * @return true if the input is valid
     */
    private boolean isInputValid() {
        //TODO validate
        String errorMessage = "";

        if (yearField.getText().length() == 0) {
            errorMessage += "Не введен год выпуска\n";
        }
        if (themeField.getText().length() == 0) {
            errorMessage += "Не введена тематика\n";
        }


        if (seriesField.getText().length() == 0) {
            errorMessage += "Не введена серия\n";
        }

        if (priceField.getText().length() == 0) {
            errorMessage += "Не введен номинал\n";
        }

        if (editionField.getText().length() == 0) {
            errorMessage += "Не введен тираж\n";
        }

        if (separationField.getText().length() == 0) {
            errorMessage += "Не введен тип зубцовки\n";
        }

        if (sizeField.getText().length() == 0) {
            errorMessage += "Не введен размер\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Show the error message.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Некорректные данные");
            alert.setHeaderText("Пожалуйста, введите характеристики марки");
            alert.setContentText(errorMessage);

            alert.showAndWait();
        }
        return false;
    }

    public static class YearChangedListener extends NumberChangedListener {

        public YearChangedListener(TextField textField) {
            super(textField);
        }

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            super.changed(observable,oldValue,newValue);

            if (getTextField().getText().length() > 4) {
                getTextField().setText(getTextField().getText().substring(0, getTextField().getText().length() - 1));
            }

        }

    }

    public static class NumberChangedListener implements ChangeListener<Number> {
        private TextField textField;

        public NumberChangedListener(TextField textField) {
            this.textField = textField;
        }

        public TextField getTextField() {
            return textField;
        }

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

            if (newValue.intValue() > oldValue.intValue()) {
                char ch = textField.getText().charAt(oldValue.intValue());
                //Check if the new character is the number or other's
                if (!(ch >= '0' && ch <= '9')) {
                    //if it's not number then just setText to previous one
                    textField.setText(textField.getText().substring(0, textField.getText().length() - 1));
                }
            }

        }
    }
}


