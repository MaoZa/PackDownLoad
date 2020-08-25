package cn.dawnland.packdownload.model.manifest;

import lombok.Data;

import java.util.List;

/**
 * @author Created by cap_sub@dawnland.cn
 */
@Data
public class Minecraft{

    private String version;
    private List<ModLoader> modLoaders;

}
