package cn.dawnland.packdownload.controller;

import cn.dawnland.packdownload.utils.MessageUtils;
import cn.dawnland.packdownload.utils.Upgrader;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Created by cap_sub@dawnland.cn
 */
public class UpdateController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            new Thread(Upgrader::download).start();
        } catch (Exception e) {
            MessageUtils.error(e);
            System.exit(0);
        }
    }

}
