package cn.dawnland.packdownload.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class FileToObjectUtils {

    public Object read(Path path, Class clazz){
        String s = FileUtils.readJsonData(path);
        return JSONObject.parseObject(s, clazz);
    }

    @SneakyThrows
    public void save(Path path, Object obj){
        try(FileOutputStream fos = new FileOutputStream(path.toFile());
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8)){
            PrintWriter pw = new PrintWriter(osw);
            pw.println(JSONObject.toJSONString(obj));
            pw.flush();
            pw.close();
        }
    }

}
