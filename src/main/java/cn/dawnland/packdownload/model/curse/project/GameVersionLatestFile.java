package cn.dawnland.packdownload.model.curse.project;

import lombok.Data;

/**
 * @author Created by cap_sub@dawnland.cn
 */
@Data
public class GameVersionLatestFile {

    private Integer fileType;
    private String gameVersion;
    private Object gameVersionFlavor;
    private Long projectFileId;
    private String projectFileName;

}
