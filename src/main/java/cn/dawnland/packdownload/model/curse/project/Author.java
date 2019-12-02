package cn.dawnland.packdownload.model.curse.project;

import lombok.Data;

/**
 * @author Created by cap_sub@dawnland.cn
 */
@Data
public class Author {

    private Long id;
    private Long userId;
    private String name;
    private Long projectId;
    private Long projectTitleId;
    private String projectTitleTitle;
    private Long twitchId;
    private String url;

}
