package com.philately;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.rtf.RtfWriter2;
import com.philately.mark.MarkParamsCache;
import com.philately.model.*;
import com.philately.model.Collection;
import com.philately.model.Color;
import com.philately.model.Currency;
import com.philately.view.DoubleTextField;
import com.philately.view.IntegerTextField;
import com.philately.view.MarkListCell;
import com.philately.view.Property;
import com.philately.view.converters.ColorClassConverter;
import com.philately.view.converters.CountryClassConverter;
import com.philately.view.converters.CurrencyClassConverter;
import com.philately.view.converters.PaperClassConverter;
import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.hibernate.Criteria;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;

import java.io.*;
import java.net.MalformedURLException;
import java.util.*;
import java.util.List;

/**
 * Created by kirill on 22.10.2015.
 */
public class MainController {

    @FXML
    private ChoiceBox countryFieldSearch;
    @FXML
    private IntegerTextField yearFieldSearch;
    @FXML
    private ChoiceBox cancellationFieldSearch;
    @FXML
    private TextField themeFieldSearch;
    @FXML
    private TextField seriesFieldSearch;
    @FXML
    private DoubleTextField priceFieldSearch;
    @FXML
    private IntegerTextField editionFieldSearch;
    @FXML
    private TextField separationFieldSearch;
    @FXML
    private ChoiceBox paperFieldSearch;
    @FXML
    private TextField sizeFieldSearch;
    @FXML
    private ChoiceBox colorFieldSearch;
    @FXML
    private ChoiceBox currencyFieldSearch;
    @FXML
    private ChoiceBox collectionFieldSearch;

    @FXML
    private ListView marksListView;

    @FXML
    private ImageView markImage;

    @FXML
    private ToolBar markToolbar;
    @FXML
    private VBox marksListVBox;
    @FXML
    private VBox markVBoxPanel;

    @FXML
    private ToggleButton inStockToggle;
    @FXML
    private VBox inStockParamsVBox;
    @FXML
    private TextArea inStockInformationTextArea;
    @FXML
    private IntegerTextField inStockCountIntegerField;


    private Mark selectedMark;


    // Reference to the main application.
    private MainApp mainApp;

    //after fxml has been loaded
    @FXML
    private void initialize() {

        setMark(null);
        setChoiceBoxes();
        handleSearch();

        marksListView.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                return new MarkListCell();
            }
        });

        marksListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                selectedMark = (Mark) marksListView.getSelectionModel().getSelectedItem();
                setMark(selectedMark);
            }
        });

        marksListVBox.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                widthResize();
            }
        });

        marksListVBox.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                heightResize();
            }
        });

        //таблица с параметрами марки
        nameColumn.setCellValueFactory(new PropertyValueFactory<Property, String>("name"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<Property, String>("value"));
        paramsData.add(new Property("Страна", ""));
        paramsData.add(new Property("Год выпуска", ""));
        paramsData.add(new Property("Гашение", ""));
        paramsData.add(new Property("Тема", ""));
        paramsData.add(new Property("Серия", ""));
        paramsData.add(new Property("Номинал", ""));
        paramsData.add(new Property("Тираж", ""));
        paramsData.add(new Property("Зубцовка", ""));
        paramsData.add(new Property("Бумага", ""));
        paramsData.add(new Property("Размер", ""));
        paramsData.add(new Property("Цвет", ""));
        markViewTable.setItems(paramsData);

        inStockToggle.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Session session = HibernateUtil.getSession();
                session.beginTransaction();
                Collection collection = null;
                if (!inStockToggle.isSelected()) {

                    // delete collection
                    if (selectedMark.getCollection() != null) {
                        session.delete(selectedMark.getCollection());
                    }
                    selectedMark.setCollection(null);
                } else {
                    // create collection
                    collection = new Collection();
                    collection.setAmount(1);
                    collection.setInfo("");
                    session.save(collection);
                    selectedMark.setCollection(collection);
                }

                session.save(selectedMark);
                session.getTransaction().commit();
                if (selectedMark != null) {
                    setCollection(selectedMark.getCollection());
                }
            }
        });

        inStockParamsVBox.managedProperty().bind(inStockParamsVBox.visibleProperty());

    }

    private void setChoiceBoxes() {
        Country defaultCountry = new Country();
        defaultCountry.setId(-1);
        defaultCountry.setTitle("-");
        countryFieldSearch.setItems(FXCollections.observableList(getArrayWithDefaultElement(defaultCountry, MarkParamsCache.getInstance().getCountries())));
        countryFieldSearch.getSelectionModel().selectFirst();
        countryFieldSearch.setConverter(new CountryClassConverter());

        Color defaultColor = new Color();
        defaultColor.setId(-1);
        defaultColor.setTitle("-");
        colorFieldSearch.setItems(FXCollections.observableList(getArrayWithDefaultElement(defaultColor, MarkParamsCache.getInstance().getColors())));
        colorFieldSearch.getSelectionModel().selectFirst();
        colorFieldSearch.setConverter(new ColorClassConverter());

        Paper defaultPaper = new Paper();
        defaultPaper.setId(-1);
        defaultPaper.setTitle("-");
        paperFieldSearch.setItems(FXCollections.observableList(FXCollections.observableList(getArrayWithDefaultElement(defaultPaper, MarkParamsCache.getInstance().getPapers()))));
        paperFieldSearch.getSelectionModel().selectFirst();
        paperFieldSearch.setConverter(new PaperClassConverter());

        Currency defaultCurrency = new Currency();
        defaultCurrency.setId(-1);
        defaultCurrency.setTitle("-");
        currencyFieldSearch.setItems(FXCollections.observableList(FXCollections.observableList(getArrayWithDefaultElement(defaultCurrency, MarkParamsCache.getInstance().getCurrency()))));
        currencyFieldSearch.getSelectionModel().selectFirst();
        currencyFieldSearch.setConverter(new CurrencyClassConverter());
    }

    public void widthResize() {
        marksListView.setPrefWidth(marksListVBox.getWidth());
    }

    public void heightResize() {
        marksListView.setPrefHeight(marksListVBox.getHeight());
    }


    private List getArrayWithDefaultElement(Object elem, List list) {
        List items = new ArrayList<>();
        items.add(elem);
        items.addAll(list);
        return items;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void handleOnSaveCollection() {
        if ((selectedMark == null) || (selectedMark.getCollection() == null)) {
            return;
        }

        if ((inStockCountIntegerField.getText().length() == 0) || ((inStockCountIntegerField.getText().length() > 0) && (inStockCountIntegerField.getInt() <= 0))) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("Некорректные данные");
            alert.setHeaderText("Пожалуйста, введите характеристики марки");
            alert.setContentText("Количество экземпляров должно быть больше нуля");
            alert.showAndWait();
            return;
        }

        selectedMark.getCollection().setInfo(inStockInformationTextArea.getText());
        selectedMark.getCollection().setAmount(inStockCountIntegerField.getInt());

        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        session.update(selectedMark.getCollection());
        session.getTransaction().commit();
    }

    @FXML
    private void handleExit() {
        mainApp.shutdown();
    }

    @FXML
    private void handleNewMark() {
        boolean okClicked = mainApp.showMarkEditDialog(null);
        if (okClicked) {
            handleSearch();
        }
    }

    /**
     * Called when the user clicks the edit button. Opens a dialog to edit
     * details for the selected person.
     */
    @FXML
    private void handleEditMark() {
        if (selectedMark != null) {
            boolean okClicked = mainApp.showMarkEditDialog(selectedMark);
            if (okClicked) {
                handleSearch();
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
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Удалить марку из справочника?", ButtonType.YES, ButtonType.NO);
        alert.initOwner(mainApp.getPrimaryStage());
        alert.setTitle("Удаление");
        alert.setHeaderText("Вы уверены?");
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {

            Session session = HibernateUtil.getSession();
            session.beginTransaction();
            Collection collection = selectedMark.getCollection();
            session.delete(selectedMark);
            if (collection != null) {
                session.delete(selectedMark);
            }
            session.getTransaction().commit();
            try {
                Utility.getInstance().getFileImage(selectedMark.getId().toString()).delete();
            } catch (Exception ex) {
            }
            selectedMark = null;
            handleSearch();
        }
    }

    @FXML
    private void handleSearch() {
        marksListView.setItems(FXCollections.observableList(searchMark()));
        marksListView.refresh();
        setMark(selectedMark);
    }

    private List searchMark(){
        Criteria criteria = HibernateUtil.getSession().createCriteria(Mark.class);
        if (((Country) countryFieldSearch.getSelectionModel().getSelectedItem()).getId() >= 0) {
            criteria = criteria.add(Restrictions.eq("country", countryFieldSearch.getSelectionModel().getSelectedItem()));
        }

        if (yearFieldSearch.getText().length() > 0) {
            criteria = criteria.add(Restrictions.eq("year", yearFieldSearch.getInt()));
        }

        if (cancellationFieldSearch.getSelectionModel().getSelectedIndex() > 0) {
            criteria = criteria.add(Restrictions.eq("cancellation", (cancellationFieldSearch.getSelectionModel().getSelectedIndex()) == 1));
        }

        if (themeFieldSearch.getText().length() > 0) {
            criteria = criteria.add(Restrictions.eq("theme", themeFieldSearch.getText()));
        }

        if (seriesFieldSearch.getText().length() > 0) {
            criteria = criteria.add(Restrictions.eq("series", seriesFieldSearch.getText()));
        }

        if (priceFieldSearch.getText().length() > 0) {
            criteria = criteria.add(Restrictions.eq("price", priceFieldSearch.getDouble()));
        }

        if (editionFieldSearch.getText().length() > 0) {
            criteria = criteria.add(Restrictions.eq("edition", editionFieldSearch.getInt()));
        }

        if (separationFieldSearch.getText().length() > 0) {
            criteria = criteria.add(Restrictions.eq("separation", separationFieldSearch.getText()));
        }

        if (((Paper) paperFieldSearch.getSelectionModel().getSelectedItem()).getId() >= 0) {
            criteria = criteria.add(Restrictions.eq("paper", paperFieldSearch.getSelectionModel().getSelectedItem()));
        }

        if (sizeFieldSearch.getText().length() > 0) {
            criteria = criteria.add(Restrictions.eq("size", sizeFieldSearch.getText()));
        }

        if (((Color) colorFieldSearch.getSelectionModel().getSelectedItem()).getId() >= 0) {
            criteria = criteria.add(Restrictions.eq("color", colorFieldSearch.getSelectionModel().getSelectedItem()));
        }

        if (((Currency) currencyFieldSearch.getSelectionModel().getSelectedItem()).getId() >= 0) {
            criteria = criteria.add(Restrictions.eq("currency", currencyFieldSearch.getSelectionModel().getSelectedItem()));
        }


        if (collectionFieldSearch.getSelectionModel().getSelectedIndex() == 1) {
            criteria = criteria.add(Restrictions.isNotNull("collection"));
        } else if (collectionFieldSearch.getSelectionModel().getSelectedIndex() == 2) {
            criteria = criteria.add(Restrictions.isNull("collection"));
        }

        return criteria.list();
    }

    @FXML
    private TableView markViewTable;
    @FXML
    private TableColumn<Property, String> nameColumn;
    @FXML
    private TableColumn<Property, String> valueColumn;
    private ObservableList<Property> paramsData = FXCollections.observableArrayList();

    private void setMark(Mark mark) {
        if (mark == null) { //hide
            markToolbar.setVisible(false);
            markVBoxPanel.setVisible(false);
        } else {
            markToolbar.setVisible(true);
            markVBoxPanel.setVisible(true);

            setCollection(mark.getCollection());

            markImage.setImage(mark.getImage());
            paramsData.get(0).setValue(mark.getCountry().getTitle());
            paramsData.get(1).setValue(String.valueOf(mark.getYear()));
            paramsData.get(2).setValue(mark.isCancellation() ? "Да" : "Нет");
            paramsData.get(3).setValue(mark.getTheme());
            paramsData.get(4).setValue(mark.getSeries());
            paramsData.get(5).setValue(mark.getPrice() + " " + mark.getCurrency().getTitle());
            paramsData.get(6).setValue(String.valueOf(mark.getEdition()));
            paramsData.get(7).setValue(mark.getSeparation());
            paramsData.get(8).setValue(mark.getPaper().getTitle());
            paramsData.get(9).setValue(mark.getSize());
            paramsData.get(10).setValue(mark.getColor().getTitle());
            markViewTable.refresh();
        }
    }

    private void setCollection(Collection collection) {
        if (collection != null) {
            inStockToggle.setSelected(true);
            inStockCountIntegerField.setText(String.valueOf(collection.getAmount()));
            inStockInformationTextArea.setText(collection.getInfo());
            inStockParamsVBox.setVisible(true);
        } else {
            inStockToggle.setSelected(false);
            inStockParamsVBox.setVisible(false);
        }
    }

    @FXML
    private void handleHelp() {
        HostServicesFactory.getInstance(mainApp).showDocument(Utility.getInstance().getFullPathToFile("help.html"));
    }

    private void createXLSReport(String path) {
        try {
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("FirstSheet");

            HSSFRow rowhead = sheet.createRow((short) 0);
            rowhead.createCell(0).setCellValue("№");
            rowhead.createCell(1).setCellValue("Характеристика");
            rowhead.createCell(2).setCellValue("Значение");

            HSSFRow row = null;
            short i = 1;
            int number = 1;
            for (Mark mark : (List<Mark>) searchMark()) {
                row = sheet.createRow(i++);
                row.createCell(0).setCellValue(number++);
                row.createCell(1).setCellValue("Страна");
                row.createCell(2).setCellValue(mark.getCountry().getTitle());
                row = sheet.createRow(i++);
                row.createCell(1).setCellValue("Год выпуска");
                row.createCell(2).setCellValue(mark.getYear());
                row = sheet.createRow(i++);
                row.createCell(1).setCellValue("Гашение");
                row.createCell(2).setCellValue((mark.isCancellation()) ? "Да" : "Нет");
                row = sheet.createRow(i++);
                row.createCell(1).setCellValue("Тематика");
                row.createCell(2).setCellValue(mark.getTheme());
                row = sheet.createRow(i++);
                row.createCell(1).setCellValue("Серия");
                row.createCell(2).setCellValue(mark.getSeries());
                row = sheet.createRow(i++);
                row.createCell(1).setCellValue("Номинал");
                row.createCell(2).setCellValue(mark.getPrice() + mark.getCurrency().getTitle());
                row = sheet.createRow(i++);
                row.createCell(1).setCellValue("Тираж");
                row.createCell(2).setCellValue(mark.getEdition());
                row = sheet.createRow(i++);
                row.createCell(1).setCellValue("Зубцовка");
                row.createCell(2).setCellValue(mark.getSeparation());
                row = sheet.createRow(i++);
                row.createCell(1).setCellValue("Бумага");
                row.createCell(2).setCellValue(mark.getPaper().getTitle());
                row = sheet.createRow(i++);
                row.createCell(1).setCellValue("Размер");
                row.createCell(2).setCellValue(mark.getSize());
                row = sheet.createRow(i++);
                row.createCell(1).setCellValue("Цвет");
                row.createCell(2).setCellValue(mark.getColor().getTitle());
                row = sheet.createRow(i++);
                row.createCell(1).setCellValue("");
                row.createCell(2).setCellValue("");
            }
            FileOutputStream fileOut = new FileOutputStream(path);
            workbook.write(fileOut);
            fileOut.close();

        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    private void createRTFReport(String path) {
        Document document = new Document();
        try {
            RtfWriter2.getInstance(document, new FileOutputStream(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        document.open();

        Font fontParamTitle = FontFactory.getFont(FontFactory.HELVETICA, Font.DEFAULTSIZE, Font.BOLD);
        fontParamTitle.setColor(java.awt.Color.darkGray);
        Font fontParamHeader = FontFactory.getFont(FontFactory.HELVETICA, Font.DEFAULTSIZE + 2, Font.BOLDITALIC);
        Font fontParamValue = FontFactory.getFont(FontFactory.HELVETICA, Font.DEFAULTSIZE, Font.NORMAL);
        // PdfPTable table = new PdfPTable(2);
        PdfPTable params = null;

        try {
            for (Mark mark : (List<Mark>) searchMark()) {
                params = new PdfPTable(2);
                params.setTotalWidth(100);
                params.addCell(new Phrase("Характеристика", fontParamHeader));
                params.addCell(new Phrase("Значение", fontParamHeader));
                params.addCell(new Phrase("Изображение", fontParamTitle));
                params.addCell(com.lowagie.text.Image.getInstance(mark.getImageUrl()));
                params.addCell(new Phrase("Страна", fontParamTitle));
                params.addCell(new Phrase(mark.getCountry().getTitle(), fontParamValue));
                params.addCell(new Phrase("Год выпуска", fontParamTitle));
                params.addCell(new Phrase(String.valueOf(mark.getYear()), fontParamValue));
                params.addCell(new Phrase("Гашение", fontParamTitle));
                params.addCell(new Phrase((mark.isCancellation()) ? "Да" : "Нет", fontParamValue));
                params.addCell(new Phrase("Тема", fontParamTitle));
                params.addCell(new Phrase(mark.getTheme(), fontParamValue));
                params.addCell(new Phrase("Серия", fontParamTitle));
                params.addCell(new Phrase(mark.getSeries(), fontParamValue));
                params.addCell(new Phrase("Номинал", fontParamTitle));
                params.addCell(new Phrase(mark.getPrice() + mark.getCurrency().getTitle(), fontParamValue));
                params.addCell(new Phrase("Тираж", fontParamTitle));
                params.addCell(new Phrase(mark.getEdition() + " шт.", fontParamValue));
                params.addCell(new Phrase("Зубцовка", fontParamTitle));
                params.addCell(new Phrase(mark.getSeparation(), fontParamValue));
                params.addCell(new Phrase("Бумага", fontParamTitle));
                params.addCell(new Phrase(mark.getPaper().getTitle(), fontParamValue));
                params.addCell(new Phrase("Размер", fontParamTitle));
                params.addCell(new Phrase(mark.getSize(), fontParamValue));
                params.addCell(new Phrase("Цвет", fontParamTitle));
                params.addCell(new Phrase(mark.getColor().getTitle(), fontParamValue));
                params.setHeaderRows(1);
                // table.addCell(new PdfPCell(params));
                //table.completeRow();
                document.add(params);

            }
            // document.add(table);


        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        document.close();
    }

    @FXML
    private void handleOnCreateReport() {

        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Отчет rtf", "*.rtf"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Отчет xls", "*.xls"));
        fileChooser.setTitle("Сохранить отчет");
        File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());
        if (file == null) {
            return;
        }

        if (fileChooser.getSelectedExtensionFilter().getDescription().equals("Отчет rtf")) {
            createRTFReport(file.getPath());
        } else {
            createXLSReport(file.getPath());
        }

    }

    @FXML
    public void handleEditPapers() {
        mainApp.showParamEditDialog(Paper.class, "Виды бумаги");
        resetInputs();
    }

    @FXML
    public void handleEditCountries() {
        mainApp.showParamEditDialog(Country.class, "Виды стран");
        resetInputs();
    }

    @FXML
    public void handleEditCurrencies() {
        mainApp.showParamEditDialog(Currency.class, "Виды валют");
        resetInputs();
    }

    @FXML
    public void handleEditColors() {
        mainApp.showParamEditDialog(Color.class, "Виды цветности");
        resetInputs();

    }

    private void resetInputs() {
        setChoiceBoxes();
        selectedMark = null;
        handleSearch();
    }

    @FXML
    public void handleAboutDialog() {

        // Load the fxml file and create a new stage for the popup dialog.
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("/fxml/about.fxml"));
        VBox page = null;
        try {
            page = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Create the dialog Stage.
        Stage dialogStage = new Stage();

        dialogStage.setTitle("О программе");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(mainApp.getPrimaryStage());
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);
        dialogStage.setResizable(false);

        // Show the dialog and wait until the user closes it
        dialogStage.showAndWait();
    }
}
