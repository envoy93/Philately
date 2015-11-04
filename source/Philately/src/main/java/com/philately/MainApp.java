package com.philately;

import com.philately.model.Country;
import com.philately.model.HibernateUtil;
import com.philately.model.Mark;
import com.philately.model.Stock;
import com.sun.javafx.tk.Toolkit;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.hibernate.classic.Session;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by kirill on 22.10.2015.
 */
public class MainApp extends Application {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        launch(args);
    }


    public void showApp() throws Exception {
        String fxmlFile = "/fxml/hello.fxml";
        FXMLLoader loader = new FXMLLoader();
        Parent root = (Parent) loader.load(getClass().getResourceAsStream(fxmlFile));
        Stage stage = new Stage();
        stage.setTitle("Maven + JavaFX + Hibernate + H2");
        stage.setScene(new Scene(root));
        stage.initStyle(StageStyle.DECORATED);
        stage.centerOnScreen();
        stage.show();

        Session session = HibernateUtil.getSessionFactory().openSession();

        session.beginTransaction();
        /*Stock stock = new Stock();

        stock.setStockCode("4715");
        stock.setStockName("GENM");

        session.save(stock);*/
        Country country = new Country();
        country.setTitle("123");
        session.save(country);
        session.getTransaction().commit();
        session.close();
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
                Thread.sleep(2000l); // just emulates some loading time
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

}
