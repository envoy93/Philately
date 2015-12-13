package com.philately;

import com.philately.mark.MarkParamsCache;
import com.philately.model.HibernateUtil;
import com.philately.model.Mark;
import com.philately.model.Paper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.io.IOException;

/**
 * Created by kirill on 22.10.2015.
 */
public class MainApp extends Application {

    private Stage primaryStage;

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        launch(args);
    }


    public void showApp() throws Exception {
        // StyleManager.getInstance().addUserAgentStylesheet(FlatterFX.class.getResource("flatterfx.css").toExternalForm());
        String fxmlFile = "/fxml/main.fxml";
        FXMLLoader loader = new FXMLLoader();
        Parent root = (Parent) loader.load(getClass().getResourceAsStream(fxmlFile));
        ((MainController) loader.getController()).setMainApp(this);
        primaryStage = new Stage();
        primaryStage.setTitle("Учет почтовых марок");
        primaryStage.setScene(new Scene(root));
        primaryStage.getScene().widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                ((MainController) loader.getController()).widthResize();
            }
        });

        primaryStage.getScene().heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                ((MainController) loader.getController()).heightResize();
            }
        });
        ///stage.getScene().getStylesheets().add("/fxml/main.css");
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.setMinHeight(780);
        primaryStage.setMinWidth(800);
        primaryStage.centerOnScreen();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                event.consume();
                shutdown();

            }
        });
        primaryStage.show();
    }


    private void loadItems(final Stage splashStage, final ProgressIndicator loadingIndicator) {
        if (loadingIndicator.isVisible()) {
            return;
        }

        loadingIndicator.setVisible(true);

        // loads the items at another thread, asynchronously
        Task loader = new Task<Boolean>() {
            {
                setOnSucceeded(workerStateEvent -> {
                    try {
                        splashStage.hide();
                        showApp();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    loadingIndicator.setVisible(false); // stop displaying the loading indicator
                });

                setOnFailed(workerStateEvent -> getException().printStackTrace());
            }

            @Override
            protected Boolean call() throws Exception {
                MarkParamsCache.getInstance(); //init cache
                return true;
            }
        };

        Thread loadingThread = new Thread(loader);
        loadingThread.setDaemon(true);
        loadingThread.start();
    }

    private boolean checkFiles() {
        if (!Utility.getInstance().isFileExist("db.h2.db.h2.db")) {
            return false;
        }

        if (!Utility.getInstance().isFileExist("default.jpg")) {
            return false;
        }

        if (!Utility.getInstance().isFileExist("help.html")) {
            return false;
        }
        return true;
    }

    @Override
    public void start(Stage primaryStage) {
        if (!checkFiles()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
         //   alert.initOwner(primaryStage);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Пожалуйста, проверьте целостность файлов приложения");
            alert.setContentText("Некоторые необходимые для работы приложения файлы отсутствуют, дальнейшая работа невозможна");

            alert.showAndWait();

            Platform.exit();
            System.exit(0);
        }


        final ProgressIndicator loadingIndicator = new ProgressIndicator();
        loadingIndicator.setPrefWidth(100);
        loadingIndicator.setPrefHeight(100);
        loadingIndicator.setVisible(false);

        VBox root = new VBox(
                new HBox(
                        new StackPane(
                                loadingIndicator
                        )
                )
        );
        root.setPadding(new Insets(20));
        primaryStage.setScene(new Scene(root, 120, 120));
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.show();
        loadItems(primaryStage, loadingIndicator);
    }

    public boolean showMarkEditDialog(Mark mark) {

        // Load the fxml file and create a new stage for the popup dialog.
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("/fxml/editForm.fxml"));
        AnchorPane page = null;
        try {
            page = (AnchorPane) loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Create the dialog Stage.
        Stage dialogStage = new Stage();

        dialogStage.setTitle("Редактировать марку");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);
        dialogStage.setResizable(false);


        // Set the person into the controller.
        EditFormController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setMark(mark);

        // Show the dialog and wait until the user closes it
        dialogStage.showAndWait();

        return controller.isOkClicked();

    }

    public void shutdown() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Выйти из приложения?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Выход");
        alert.setHeaderText("Вы уверены?");
        alert.initOwner(primaryStage);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            HibernateUtil.shutdown();
            Platform.exit();
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void showPaperEditDialog() {
        EditParamController editParamController = new EditParamController();

        // Create the dialog Stage.
        Stage dialogStage = new Stage();

        dialogStage.setTitle("Виды бумаги");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(editParamController.createForm(Paper.class));
        dialogStage.setScene(scene);
        //dialogStage.setResizable(false);
        dialogStage.setMinHeight(400);

        dialogStage.setMaxHeight(dialogStage.getWidth());
        dialogStage.setMinWidth(300);
        editParamController.setDialogStage(dialogStage);
        // Show the dialog and wait until the user closes it
        dialogStage.showAndWait();
    }
}
