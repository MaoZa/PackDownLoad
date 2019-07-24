package cn.dawnland.packdownload.model;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;

public class DownloadProgress extends HBox {

    private Label text;
    private Label name;
    private ProgressBar progressBar = new ProgressBar();
    private Label speed = new Label();
    private Label percentage = new Label("0%");

    public DownloadProgress(ProgressType type, Label name) {
        this.text = new Label(type.toString());
        this.name = name;
        this.getChildren().addAll(text, name, progressBar, speed, progressBar);
    }


}


