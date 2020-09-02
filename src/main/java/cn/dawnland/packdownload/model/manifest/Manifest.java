package cn.dawnland.packdownload.model.manifest;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author Created by cap_sub@dawnland.cn
 */
@Data
public class Manifest {

    private Minecraft minecraft;
    private Integer manifestVersion;
    private String name;
    private String version;
    private String author;
    private List<ManifestFile> files;
    private String overrides;

    private String thisJsonFilePath;

    public void save(){
        synchronized (this){
            File file = Paths.get(thisJsonFilePath).toFile();
            try(FileWriter fileWriter = new FileWriter(file, false);) {
                fileWriter.write(JSONObject.toJSONString(this));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

