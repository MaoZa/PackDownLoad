package cn.dawnland.packdownload.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
