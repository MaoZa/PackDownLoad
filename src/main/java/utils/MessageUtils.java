package utils;

import javafx.application.Platform;

import javax.swing.*;

/**
 * @author Cap_Sub
 * @version 创建时间：2019/05/30 14:42
 */
public class MessageUtils {

    public static void error(Exception e){
        Platform.runLater(() -> JOptionPane.showMessageDialog(null, "未知错误", e.getStackTrace().toString(), 0));
        e.printStackTrace();
    }

    public static void error(String msg, String title){
        Platform.runLater(() -> JOptionPane.showMessageDialog(null, msg, title, 0));
    }

    public static void info(String msg, String title){
        Platform.runLater(() -> JOptionPane.showMessageDialog(null, msg, title, 1));
    }

    public static void info (String msg){
        Platform.runLater(() -> JOptionPane.showMessageDialog(null, msg));
    }

}
