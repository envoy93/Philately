package com.philately;

import com.philately.mark.MarkParamsCache;
import com.philately.model.*;
import com.sun.prism.impl.Disposer;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.hibernate.Criteria;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by kirill on 13.12.2015.
 */
public class EditParamController {
    private TableView<Row> tableView = new TableView<Row>();
    private Class classType = null;
    private Stage dialogStage;

    private ObservableList<Row> data = null;

    public VBox createForm(Class classType) {
        this.classType = classType;

        tableView.setEditable(false);
        TableColumn<Row, String> title = new TableColumn<Row, String>("Название");
        title.setCellValueFactory(new PropertyValueFactory<Row, String>("title"));
        tableView.getColumns().add(title);

        //Insert Button
        TableColumn col_action = new TableColumn<>("Действие");
        tableView.getColumns().add(col_action);

        col_action.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Row, Boolean>,
                        ObservableValue<Boolean>>() {

                    @Override
                    public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Row, Boolean> p) {
                        return new SimpleBooleanProperty(p.getValue() != null);
                    }
                }
        );

        //Adding the Button to the cell
        col_action.setCellFactory(
                new Callback<TableColumn<Row, Boolean>, TableCell<Row, Boolean>>() {

                    @Override
                    public TableCell<Row, Boolean> call(TableColumn<Row, Boolean> p) {
                        return new ButtonCell();
                    }

                }
        );

        Button okButton = new Button("OK");
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dialogStage.close();
            }
        });


        Button createButton = new Button("Добавить");
        createButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TextInputDialog dialog = new TextInputDialog("");
                dialog.setTitle("Создание варианта");
                dialog.setHeaderText("Создание варианта");
                dialog.setContentText("Введите значение параметра:");

                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    Criteria criteria = HibernateUtil.getSession().createCriteria(classType);
                    criteria.add(Restrictions.eq("title", result.get()));

                    if (criteria.list().size() == 0) {
                        Session session = HibernateUtil.getSession();
                        session.beginTransaction();
                        ParamWithTitle paramWithTitle = null;
                        try {
                            paramWithTitle = (ParamWithTitle) classType.getConstructor().newInstance();
                            paramWithTitle.setTitle(result.get());
                            session.save(paramWithTitle);
                        } catch (Exception e) {
                        }
                        session.getTransaction().commit();

                        data.add(new Row(result.get()));
                        tableView.refresh();
                        updateCache();
                    } else {
                        Alert alert1 = new Alert(Alert.AlertType.ERROR);
                        alert1.setTitle("Ошибка");
                        alert1.initOwner(dialogStage);
                        alert1.setHeaderText("Совпадение параметров");
                        alert1.setContentText("Данный параметр уже существует!");

                        alert1.showAndWait();
                    }
                }
            }
        });

        initItems();
        tableView.setItems(data);

        VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(5));
        vBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
        vBox.getChildren().add(createButton);
        vBox.getChildren().addAll(tableView);
        vBox.getChildren().add(okButton);

        return vBox;
    }

    private void initItems() {
        List<Row> rows = new ArrayList<>();
        if (classType.isAssignableFrom(Paper.class)) {
            rows.addAll(MarkParamsCache.getInstance().getPapers().stream().map(param -> new Row(param.getTitle())).collect(Collectors.toList()));
        } else if (classType.isAssignableFrom(Color.class)) {
            rows.addAll(MarkParamsCache.getInstance().getColors().stream().map(param -> new Row(param.getTitle())).collect(Collectors.toList()));
        } else if (classType.isAssignableFrom(Country.class)) {
            rows.addAll(MarkParamsCache.getInstance().getCountries().stream().map(param -> new Row(param.getTitle())).collect(Collectors.toList()));
        } else if (classType.isAssignableFrom(Currency.class)) {
            rows.addAll(MarkParamsCache.getInstance().getCurrency().stream().map(param -> new Row(param.getTitle())).collect(Collectors.toList()));
        }

        data = FXCollections.observableArrayList(rows);
    }


    public static class Row {

        private final SimpleStringProperty title;

        private Row(String title) {
            this.title = new SimpleStringProperty(title);
        }

        public String getTitle() {
            return title.get();
        }

        public SimpleStringProperty titleProperty() {
            return title;
        }

        public void setTitle(String title) {
            this.title.set(title);
        }
    }

    //Define the button cell
    private class ButtonCell extends TableCell<Row, Boolean> {
        final Button deleteButton = new Button();
        final Button editButton = new Button();

        String paramName = "";
        ParamWithTitle paramWithTitle = null;

        private void setValues(Row currentRow) {
            if (classType.isAssignableFrom(Paper.class)) {
                paramName = "paper";
                paramWithTitle = MarkParamsCache.getInstance().getPaper(currentRow.getTitle());
            } else if (classType.isAssignableFrom(Country.class)) {
                paramName = "country";
                paramWithTitle = MarkParamsCache.getInstance().getCountry(currentRow.getTitle());
            } else if (classType.isAssignableFrom(Currency.class)) {
                paramName = "currency";
                paramWithTitle = MarkParamsCache.getInstance().getCurrency(currentRow.getTitle());
            } else if (classType.isAssignableFrom(Color.class)) {
                paramName = "color";
                paramWithTitle = MarkParamsCache.getInstance().getColor(currentRow.getTitle());
            }
        }

        ButtonCell() {
            try {
                deleteButton.setGraphic(new ImageView(MainApp.class.getResource("/img/delete.png").toURI().toString()));
                editButton.setGraphic(new ImageView(MainApp.class.getResource("/img/edit.png").toURI().toString()));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            deleteButton.setMaxWidth(16);
            deleteButton.setMaxHeight(16);
            editButton.setMaxWidth(16);
            editButton.setMaxHeight(16);

            deleteButton.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent t) {
                    Row currentRow = getTableView().getItems().get(getIndex());
                    setValues(currentRow);

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Удалить вариант?", ButtonType.YES, ButtonType.NO);
                    alert.initOwner(dialogStage);
                    alert.setTitle("Удаление");
                    alert.setHeaderText("Вы уверены?");
                    alert.showAndWait();
                    if (alert.getResult() == ButtonType.YES) {
                        Criteria criteria = HibernateUtil.getSession().createCriteria(Mark.class);
                        criteria.add(Restrictions.eq(paramName, paramWithTitle));

                        if (criteria.list().size() == 0) {
                            Session session = HibernateUtil.getSession();
                            session.beginTransaction();
                            session.delete(paramWithTitle);
                            session.getTransaction().commit();
                            data.remove(currentRow);
                            tableView.refresh();

                            updateCache();
                        } else {
                            Alert alert1 = new Alert(Alert.AlertType.ERROR);
                            alert1.setTitle("Ошибка");
                            alert1.initOwner(dialogStage);
                            alert1.setHeaderText("Удаление используемого параметра");
                            alert1.setContentText("Данный параметр уже используется, удалить его невозможно!");

                            alert1.showAndWait();
                        }

                    }

                }
            });

            editButton.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent t) {
                    Row currentRow = getTableView().getItems().get(getIndex());
                    setValues(currentRow);

                    TextInputDialog dialog = new TextInputDialog(currentRow.getTitle());
                    dialog.setTitle("Редактирование варианта");
                    dialog.setHeaderText("Редактирование варианта");
                    dialog.setContentText("Введите значение параметра:");

                    Optional<String> result = dialog.showAndWait();
                    if (result.isPresent()) {
                        Criteria criteria = HibernateUtil.getSession().createCriteria(classType);
                        criteria.add(Restrictions.eq("title", result.get()));

                        if (criteria.list().size() == 0) {
                            Session session = HibernateUtil.getSession();
                            session.beginTransaction();
                            paramWithTitle.setTitle(result.get());
                            session.update(paramWithTitle);
                            session.getTransaction().commit();

                            currentRow.setTitle(result.get());
                            updateCache();
                        } else {
                            Alert alert1 = new Alert(Alert.AlertType.ERROR);
                            alert1.setTitle("Ошибка");
                            alert1.initOwner(dialogStage);
                            alert1.setHeaderText("Совпадение параметров");
                            alert1.setContentText("Данный параметр уже существует!");

                            alert1.showAndWait();
                        }
                    }

                }
            });
        }


        //Display button if the row is not empty
        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);
            if (!empty) {
                HBox hBox = new HBox();
                hBox.getChildren().add(editButton);
                hBox.getChildren().add(deleteButton);

                setGraphic(hBox);
            }
        }
    }

    private void updateCache() {
        MarkParamsCache.getInstance().reCache();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

}
