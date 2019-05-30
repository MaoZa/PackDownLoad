package utils;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;

import javax.swing.*;
import java.io.UnsupportedEncodingException;

/**
 * @author Cap_Sub
 */
public class UIUpdateUtils {

    public static void textAreaAppend(TextArea logText, String appendStr) {
        Platform.runLater(() -> {
            logText.setText(encode(appendStr));
        });
    }

    public static String encode(String souStr){
        try {
            return new String(souStr.getBytes(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            MessageUtils.error(e);
            e.printStackTrace();
        }
        return null;
    }


    public static void updateProgress(ProgressBar progressBar, int count){
        Platform.runLater(() -> {
            progressBar.setProgress(progressBar.getProgress() + (1D/count));
        });
    }

    public static void updateLable(Label label, String values){
        Platform.runLater(() -> {
            label.setText(values);
        });
    }

}
