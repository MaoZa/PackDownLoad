package cn.dawnland.packdownload.utils;

import cn.dawnland.packdownload.model.Version;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Cap_Sub
 * Mojang相关工具
 */
public class MojangUtils {

    private static ConcurrentHashMap<String, Version> mojangVersions;

    public static void getVersions() throws IOException {
        String url = "https://launchermeta.mojang.com/mc/game/version_manifest.json";
        String s = OkHttpUtils.get().get(url);
        JSONObject jsonObject = JSONObject.parseObject(s);
        List<Version> versions = JSONArray.parseArray(jsonObject.getString("versions"), Version.class);
        if(mojangVersions == null) {
            mojangVersions = new ConcurrentHashMap<>();
            versions.forEach(v -> mojangVersions.putIfAbsent(v.getId(), v));
        }else if(mojangVersions.size() != versions.size()){
            versions.forEach(v -> mojangVersions.putIfAbsent(v.getId(), v));
        }
    }

    public static Version getVersionUrlByVersion(String version) throws IOException {
        if(Objects.isNull(mojangVersions)){
            getVersions();
        }
        return mojangVersions.get(version);
    }

    public static String getJsonUrl(String version) throws IOException {
        return getVersionUrlByVersion(version).getUrl();
    }

}
