package cn.dawnland.packdownload.model.curse.project;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author Created by cap_sub@dawnland.cn
 */
@Data
public class LatestFile {

    private Long id;
    private Integer alternateFileId;
    private Integer categorySectionPackageType;
    private Object changelog;
    private Object[] dependencies;
    private String displayName;
    private String downloadUrl;
    private Object exposeAsAlternative;
    private ZonedDateTime fileDate;
    private Long fileLegacyMappingId;
    private Long fileLength;
    private String fileName;
    private Integer fileStatus;
    private Integer fileTypeId;
    private Integer gameId;
    private List<String> gameVersion;
    private ZonedDateTime gameVersionDateReleased;
    private Object gameVersionFlavor;
    private Integer gameVersionId;
    private Long gameVersionMappingId;
    private Boolean hasInstallScript;
    private Object installMetadata;
    private Boolean isAlternate;
    private Boolean isAvailable;
    private Boolean isCompatibleWithClient;
    private Boolean isServerPack;
    private List<Module> modules;
    private Long packageFingerprint;
    private Long packageFingerprintId;
    private Object parentFileLegacyMappingId;
    private Object parentProjectFileId;
    private Long projectId;
    private Integer projectStatus;
    private Integer releaseType;
    private Long renderCacheId;
    private Integer restrictProjectFileAccess;
    private Long serverPackFileId;
    private List<SortableGameVersion> sortableGameVersion;

}
