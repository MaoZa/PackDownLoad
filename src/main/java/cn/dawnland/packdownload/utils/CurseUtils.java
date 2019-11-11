/*
//package cn.dawnland.packdownload.utils;
//
//import cn.dawnland.packdownload.model.Project;
//import cn.dawnland.packdownload.netty.config.NettyConfig;
//import org.jsoup.Connection;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//
//import java.io.*;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentMap;

public class CurseUtils {

//    public static Set<Project> searchProjectByName(String name) throws IOException {
//        String s = OkHttpUtils.get().get(serverurl + "/search?key=" + name);
//        HashSet<Project> projects = new HashSet<>(JSONArray.parseArray(s, Project.class));
//        return projects;
//    }
    public static Set<Project> searchProjectByName(String name) throws IOException {
//        HashSet<Project> projects = new HashSet<>(JSONArray.parseArray(s, Project.class));
//        Connection connect = Jsoup.connect(baseUrl + "/minecraft/modpacks/search?search=" + name);
        Connection connect = Jsoup.connect("https://www.curseforge.com/minecraft/modpacks/skyfactory-4/download/2767759/file");
        connect.header("X-Api-Token", "58127124-e7c5-4e29-b49b-5156e08fd0a1");
        Document document1 = connect.get();

        return null;
    }

    public static void main(String[] args) throws IOException {
        searchProjectByName("sky");
    }

    public static Document getDocumentByProjectUrl(String projectUrl){
        try {
            return Jsoup.connect(projectUrl).get();
        } catch (IOException e) {
            MessageUtils.error("不存在或无法访问", "整合包链接错误");
            e.printStackTrace();
        }
        return null;
    }

    public static ConcurrentHashMap<String, String> getProjectNameAndDownloadUrl(String projectUrl){
        Document document;
        document = CurseUtils.getDocumentByProjectUrl(projectUrl);
        Element element = document.getElementsByClass("listing listing-project-file project-file-listing b-table b-table-a")
                .get(0)
                .getElementsByTag("tbody")
                .get(0).getElementsByTag("tr").get(0)
                .getElementsByAttributeValue("data-action", "modpack-file-link").get(0);
        ConcurrentHashMap resulMap = new ConcurrentHashMap();
        if(element != null){
            resulMap.putIfAbsent("downloadUrl", baseUrl + element.attr("href").replace("files", "download") + "/file");
            resulMap.putIfAbsent("packName", element.text());
        }else{
            MessageUtils.error("出错", "获取整合包文件失败，请确认链接是否正确");
        }
        return resulMap;
    }

    public static void failsMod(ConcurrentMap<String, String> downloadFialdModS){
        String[] url = {"https://www.curseforge.com/minecraft/mc-mods/", "/download/", "/file"};
        File downloadFialdModsfile = new File(DownLoadUtils.getPackPath() + "/下载失败的MOD.txt");
        FileOutputStream fio = null;
        try {
            fio = new FileOutputStream(downloadFialdModsfile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        PrintStream ps = new PrintStream(fio);
        */
/** 循环处理下载失败的mod *//*

        ps.println("以下mod请尝试手动下载至目录:" + DownLoadUtils.getPackPath() + "/mods");
        String text = "MODID:{MODID}\tURL:{URL}";
        downloadFialdModS.forEach((p,f) ->{
            ps.println(text.replace("{MODID}", p).replace("{URL}", url[0] + p + url[1] + f + url[2]));
        });
        ps.flush();
        ps.close();
        try {
            Process p = Runtime.getRuntime().exec("notepad " + downloadFialdModsfile.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fio.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
*/
