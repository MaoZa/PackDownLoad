package cn.dawnland.packdownload.utils;

import cn.dawnland.packdownload.task.JsonJXTask;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.Objects;

/**
 * @author Cap_Sub
 * 公共工具
 */
public class CommonUtils {

    /**
     * 退出软件
     */
    public static void appExit(){
//        FileUtils.deleteTmpFile();
        if(Objects.nonNull(JsonJXTask.manifest)){
            JsonJXTask.manifest.save();
        }
        System.exit(0);
    }

    public static void setClipboardString(String text) {
        // 获取系统剪贴板
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        // 封装文本内容
        Transferable trans = new StringSelection(text);
        // 把文本内容设置到系统剪贴板
        clipboard.setContents(trans, null);
    }

}
