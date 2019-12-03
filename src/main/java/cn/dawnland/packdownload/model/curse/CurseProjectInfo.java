package cn.dawnland.packdownload.model.curse;

import cn.dawnland.packdownload.model.curse.project.*;
import cn.dawnland.packdownload.utils.OkHttpUtils;
import com.alibaba.fastjson.JSONArray;
import lombok.Data;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Created by cap_sub@dawnland.cn
 */
@Data
public class CurseProjectInfo {

    private Long id;
    private String Name;

    private List<Attachment> attachments;
    private List<Author> authors;

    private List<Categorie> categories;
    private CategorySection categorySection;

    private Long defaultFileId;
    private Long downloadCount;

    private Integer gameId;
    private String gameName;
    private Integer gamePopularityRank;
    private String gameSlug;
    private List<GameVersionLatestFile> gameVersionLatestFiles;

    private Boolean isAvailable;
    private Boolean isExperiemental;
    private Boolean isFeatured;

    private List<LatestFile> latestFiles;

    private BigDecimal popularityScore;
    private String portalName;
    private Integer primaryCategoryId;
    private String primaryLanguage;

    private String slug;
    private Integer status;
    private String summary;

    private String websiteUrl;

    private ZonedDateTime dateCreated;
    private ZonedDateTime dateModified;

    public static Map<String, Map<String, String>> searchProject(String searchText) throws IOException {
        String url = "https://addons-ecs.forgesvc.net/api/v2/addon/search?gameId=432&gameVersion=&pageSize=10&sectionId=4471&searchFilter=" + searchText;
        String s = OkHttpUtils.get().get(url);
        List<CurseProjectInfo> curseProjectInfos = JSONArray.parseArray(s, CurseProjectInfo.class);
        return curseProjectInfos.stream()
                .collect(Collectors.toMap(CurseProjectInfo::getName, curseProjectInfo ->
                        curseProjectInfo.latestFiles.stream().collect(Collectors.toMap(LatestFile::getDisplayName, LatestFile::getDownloadUrl))));
    }

}

