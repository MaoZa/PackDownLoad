package model;

import lombok.Data;

/**
 * @author Cap_Sub
 */
@Data
public class ModDownLoad {

    //Mod名
    private String name;
    //保存文件名
    private String fileName;
    //下载url
    private String url;
    //mod版本
    private String version;

}
