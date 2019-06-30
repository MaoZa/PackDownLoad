package utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import model.Version;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MojangUtils {

    private static ConcurrentHashMap<String, Version> mojangVersions;

    public static Map<String, Version> getVersions(){
        String url = "https://launchermeta.mojang.com/mc/game/version_manifest.json";
        String s = HttpUtils.get(url);
        JSONObject jsonObject = JSONObject.parseObject(s);
        List<Version> versions = JSONArray.parseArray(jsonObject.getString("versions"), Version.class);
        if(mojangVersions == null) {
            mojangVersions = new ConcurrentHashMap<>();
            versions.forEach(v -> mojangVersions.putIfAbsent(v.getId(), v));
        }else if(mojangVersions.size() != versions.size()){
            versions.forEach(v -> mojangVersions.putIfAbsent(v.getId(), v));
        }
        return mojangVersions;
    }

    public static Version getVersionUrlByVersion(String version){
        getVersions();
        return mojangVersions != null ? mojangVersions.get(version) : null;
    }

    public static String getJsonUrl(String version){
        return getVersionUrlByVersion(version).getUrl();
    }

}
