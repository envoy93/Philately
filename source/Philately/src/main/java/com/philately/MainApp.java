package com.philately;

import com.philately.mark.MarkParamsCache;
import com.philately.model.Mark;
import com.sun.javafx.css.StyleManager;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
        primaryStage.setTitle("Maven + JavaFX + Hibernate + H2");
        primaryStage.setScene(new Scene(root));
        ///stage.getScene().getStylesheets().add("/fxml/main.css");
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.centerOnScreen();
        primaryStage.show();

       /* Session session = HibernateUtil.getSessionFactory().openSession();

        session.beginTransaction();
        Stock stock = new Stock();

        stock.setStockCode("4715");
        stock.setStockName("GENM");

        session.save(stock);*/
       /* Country country = new Country();
        country.setTitle("123");
        session.save(country);
        session.getTransaction().commit();
        session.close();*/
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

    @Override
    public void start(Stage primaryStage) {
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

        // Set the person into the controller.
        EditFormController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setMark(mark);

        // Show the dialog and wait until the user closes it
        dialogStage.showAndWait();

        return controller.isOkClicked();

    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

}
