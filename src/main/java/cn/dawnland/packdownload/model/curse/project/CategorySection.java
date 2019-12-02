package cn.dawnland.packdownload.model.curse.project;

import lombok.Data;

/**
 * @author Created by cap_sub@dawnland.cn
 */
@Data
public class CategorySection {

    private Integer id;
    private String name;
    private Integer packageType;
    private String path;
    private Object extraIncludePattern;
    private Integer gameCategoryId;
    private Integer gameId;
    private String initialInclusionPattern;

}
