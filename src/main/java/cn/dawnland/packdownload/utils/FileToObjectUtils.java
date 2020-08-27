package cn.dawnland.packdownload.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.file.Path;

public class FileToObjectUtils {

    public Object read(Path path, Class clazz){
        String s = FileUtils.readJsonData(path);
        return JSONObject.parseObject(s, clazz);
    }

    @SneakyThrows
    public void save(Path path, Object obj){
        try(FileOutputStream fos = new FileOutputStream(path.toFile())){
            PrintWriter pw = new PrintWriter(fos);
            pw.println(JSONObject.toJSONString(obj));
            pw.flush();
            pw.close();
        }
    }

}
