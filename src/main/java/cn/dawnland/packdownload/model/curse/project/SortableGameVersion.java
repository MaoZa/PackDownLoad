package cn.dawnland.packdownload.model.curse.project;

import lombok.Data;

import java.time.ZonedDateTime;

/**
 * @author Created by cap_sub@dawnland.cn
 */
@Data
public class SortableGameVersion {

    private String gameVersion;
    private String gameVersionName;
    private String gameVersionPadded;
    private ZonedDateTime gameVersionReleaseDate;

}
