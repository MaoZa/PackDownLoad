package model;

import lombok.Data;

/**
 * mod下载数据实体
 * @author Cap_Sub
 */
@Data
public class ModDownLoad extends DownLoadModel {

    //Mod名
    private String name;
    //Mod版本
    private String version;
    //mc版本
    private Integer mcVersion;
    //forge版本
    private Long forgeVersion;

    @Override
    public String getFileName(){
        if(fileName == null || fileName.equals("")){
            return name + (version == null || version.equals("") ? "" : "_" + version)
                    + (mcVersion != null ? "_" + mcVersion : "")
                    + (forgeVersion != null ? "_" + forgeVersion : "") + ".jar";
        }
        if(fileName != null && !"".equals(fileName) && name != null && !"".equals(name)){
            return "[" + name + "]" + fileName + ".jar";
        }
        return getFileName();
    }

}
