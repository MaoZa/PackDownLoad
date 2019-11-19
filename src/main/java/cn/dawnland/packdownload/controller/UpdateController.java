package cn.dawnland.packdownload.controller;

import cn.dawnland.packdownload.utils.MessageUtils;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

import static cn.dawnland.packdownload.utils.Upgrader.*;

/**
 * @author Created by cap_sub@dawnland.cn
 */
public class UpdateController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ((Runnable) () -> {
            try {
                dowload();
            } catch (Exception e) {
                MessageUtils.error(e);
                System.exit(0);
            }
        }).run();
    }

}
