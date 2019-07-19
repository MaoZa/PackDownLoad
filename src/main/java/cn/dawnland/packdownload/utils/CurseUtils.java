package cn.dawnland.packdownload.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CurseUtils {

    public static String baseUrl = "https://www.curseforge.com";

    public static Elements searchProjectByName(String name){
        String searchUrl = baseUrl + "/minecraft/modpacks/" +
                "search?search=";
        searchUrl += name;
        Document document = null;
        try {
            document = Jsoup.connect(searchUrl).get();
        } catch (IOException e) {
            MessageUtils.error(e);
            e.printStackTrace();
        }
        Elements elementsByClass = document.getElementsByClass("flex items-end lg:hidden");
        return elementsByClass;
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
        String downloadUrl;
        String packName;
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
        /** 循环处理下载失败的mod */
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
