package cn.dawnland.packdownload.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstallInfo {

    private String zipFilePath;
    private String rootPath;
    private String packPath;
    private Integer therdCount;

}
