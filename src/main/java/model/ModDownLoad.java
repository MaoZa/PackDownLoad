package model;

import lombok.Data;
import utils.MessageUtils;
import utils.UIUpdateUtils;

import java.io.UnsupportedEncodingException;


/**
 * mod下载数据实体
 *
 * @author Cap_Sub
 */
@Data
public class ModDownLoad extends DownLoadModel {

    //Mod名
    private String name;
    //Mod版本
    private String version;
    //mc版本
    private String mcVersion;
    //forge版本
    private Long forgeVersion;

    @Override
    public String getFileName() {
        String filename;
        if (fileName == null || fileName.equals("")) {
            filename = name + (version == null || version.equals("") ? "" : "_" + version)
                    + (mcVersion != null ? "_" + mcVersion : "")
                    + (forgeVersion != null ? "_" + forgeVersion : "") + ".jar";

            return UIUpdateUtils.encode(filename);

        }
        if (fileName != null && !"".equals(fileName) && name != null && !"".equals(name)) {
            filename = "[" + name + "]" + fileName + ".jar";
            return UIUpdateUtils.encode(filename);
        }
        return null;
    }

    public void setName(String name) {
        try {
            byte[] bytes = name.getBytes("GBK");
            this.name = new String(bytes, "GBK");
        } catch (UnsupportedEncodingException e) {
            MessageUtils.error(e);
            e.printStackTrace();
        }
    }
}
