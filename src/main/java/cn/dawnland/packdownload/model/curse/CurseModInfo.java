package cn.dawnland.packdownload.model.curse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Created by cap_sub@dawnland.cn
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurseModInfo {

    private Integer id;
    private String displayName;
    private String fileName;
    private String fileDate;
    private Long fileLength;
    private Integer releaseType;
    private Integer fileStatus;
    private String downloadUrl;
    private Boolean isAlternate;
    private Integer alternateFileId;
    private List<Object> dependencies;
    private Boolean isAvailable;
    private Object modules;

}
