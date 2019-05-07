package utils;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;

import java.io.UnsupportedEncodingException;

/**
 * @author Cap_Sub
 */
public class UIUpdateUtils {

    public static void textAreaAppend(TextArea logText, String appendStr) throws UnsupportedEncodingException {
        Platform.runLater(() -> {
            try {
                logText.setText(logText.getText() + (encode(appendStr)));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
    }

    private static String encode(String souStr) throws UnsupportedEncodingException {
        return new String(souStr.getBytes(), "UTF-8");
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
