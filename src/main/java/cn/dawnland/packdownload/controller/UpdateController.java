package cn.dawnland.packdownload.controller;

import cn.dawnland.packdownload.utils.MessageUtils;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

import static cn.dawnland.packdownload.utils.Upgrader.dowload;

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
