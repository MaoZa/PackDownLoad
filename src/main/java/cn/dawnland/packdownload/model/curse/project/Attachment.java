package cn.dawnland.packdownload.model.curse.project;

import lombok.Data;

/**
 * @author Created by cap_sub@dawnland.cn
 */
@Data
public class Attachment {

    private Long id;
    private String description;
    private Boolean isDefault;
    private Long projectId;
    private Integer status;
    private String thumbnailUrl;
    private String title;
    private String url;

}
