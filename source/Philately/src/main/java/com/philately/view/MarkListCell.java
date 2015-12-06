package com.philately.view;

import com.philately.Utility;
import com.philately.model.Mark;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Created by kirill on 07.11.2015.
 */
public class MarkListCell extends ListCell<Mark> {
    private static final String CACHE_LIST_FOUND_CLASS = "cache-list-found";
    private static final String CACHE_LIST_NOT_FOUND_CLASS = "cache-list-not-found";
    private static final String CACHE_LIST_NAME_CLASS = "cache-list-name";
    private static final String CACHE_LIST_DT_CLASS = "cache-list-dt";
    private static final String CACHE_LIST_ICON_CLASS = "cache-list-icon";
    private static final String FONT_AWESOME = "FontAwesome";

    private GridPane grid = new GridPane();
    private ImageView image = new ImageView("file:/additionalAppResources/default10.png");
    private Label name = new Label();
    private Label dt = new Label();
    private Label price = new Label();
    private Label info = new Label();

    public MarkListCell() {
        configureGrid();
        configureName();
        configureDifficultyTerrain();
        addControlsToGrid();
    }

    private void configureGrid() {
        grid.setHgap(10);
        grid.setVgap(4);
        grid.setPadding(new Insets(0, 10, 0, 10));
    }

    private void configureName() {
        name.getStyleClass().add(CACHE_LIST_NAME_CLASS);
    }

    private void configureDifficultyTerrain() {
        dt.getStyleClass().add(CACHE_LIST_DT_CLASS);
    }

    private void addControlsToGrid() {
        grid.add(image, 0, 0, 1, 4);
        grid.add(name, 1, 0);
        grid.add(price, 1, 1);
        grid.add(dt, 1, 2);
        grid.add(info, 1, 3);
    }

    @Override
    public void updateItem(Mark mark, boolean empty) {
        super.updateItem(mark, empty);
        if (empty) {
            clearContent();
        } else {
            addContent(mark);
        }
    }

    private void clearContent() {
        setText(null);
        setGraphic(null);
    }

    private void addContent(Mark mark) {
        setText(null);
        //icon.setText(GeocachingIcons.getIcon(mark).toString());
        name.setText(mark.getTheme());
        name.setWrapText(true);
        price.setText(mark.getPrice() + mark.getCurrency().getTitle() + ", " + mark.getEdition() + "шт. в тираже");
        price.setWrapText(true);
        dt.setText(mark.getCountry().getTitle() + ", " + mark.getYear() + "г.");
        dt.setWrapText(true);
        info.setText("Серия: " + mark.getSeries() + ", бумага: " + mark.getPaper().getTitle() + ", размеры: " + mark.getSize());
        info.setWrapText(true);

        //image.setImage(new Image("file:/additionalAppResources/default.png"));
        image.setFitHeight(64);
        image.setFitWidth(64);
        image.setImage(mark.getImage());
        // setStyleClassDependingOnFoundState(mark);
        setGraphic(grid);
    }

        /*private void setStyleClassDependingOnFoundState(Mark mark) {
            if (CacheUtils.hasUserFoundCache(cache, new Long(3906456))) {
                addClasses(this, CACHE_LIST_FOUND_CLASS);
                removeClasses(this, CACHE_LIST_NOT_FOUND_CLASS);
            } else {
                addClasses(this, CACHE_LIST_NOT_FOUND_CLASS);
                removeClasses(this, CACHE_LIST_FOUND_CLASS);
            }
        }*/
}