package utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import model.DownLoadModel;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Cap_Sub
 */
public class DownLoadUtils {

    private static Pattern FilePattern = Pattern.compile("[\\\\/:*?\"<>|]");
    public static String filenameFilter(String str) {
        return str==null?null:FilePattern.matcher(str).replaceAll("");
    }

    public static Label downloadSpeed;
    private static String rootPath;
    private static String packPath;

    static {
        rootPath = DownLoadUtils.getRootPath();
    }

    public static String getPackPath(){
        if(packPath == null){
            packPath = getRootPath() + "/.minecraft";
        }
        return packPath;
    }

    public static void setPackPath(String path){
        packPath = path;
    }

    /**
     * 下载文件到指定目录 不指定path则下载到rootPath下
     * @param url 下载链接
     */
    public static String downLoadFile(String url, String path) throws IOException {
        if(path == null || "".equals(path)){
            path = getPackPath();
        }
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return DownLoadUtils.downLoadFromUrl(url, path);
    }

    /**
     * 下载整合包文件
     * @param url 下载链接
     * @param fileName 保存文件的文件名
     * @param path 相对于.minecraft的路径(.minecraft = 根目录)
     */
    public static boolean downLoadFile(String url, String fileName, String path, Long size, ProgressBar progressBar, Label proLabel, Double proSize) throws IOException {
        if(path == null || "".equals(path)){
            path = rootPath;
        }
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        DownLoadUtils.downLoadFromUrl(url, fileName, path, progressBar, proLabel, proSize);
        return true;
    }

    /**
     * 下载整合包文件
     * @param url 下载链接
     * @param fileName 保存文件的文件名
     * @param path 相对于.minecraft的路径(.minecraft = 根目录)
     */
    public static boolean downLoadFile(String url, String fileName, String path, Long size) throws IOException {
        path = rootPath + "/" + path;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        //如果传入文件size 则判断本地是否已经下载该文件且大小一致(则不下载直接返回)
        if(size != null && size > 0){
            File localFile = new File(path + "/" + fileName);
            if(localFile.exists() && size.equals(localFile.length())){
                return false;
            }
        }
        DownLoadUtils.downLoadFromUrl(url, fileName, path);
        return true;
    }


    /**
     * 获取运行目录
     * @return
     */
    public static String getRootPath(){
        if(rootPath == null){
            return System.getProperty("user.dir");
        }
        return rootPath;
    }

    public static void setRootPath(String path){
        rootPath = path;
    }

    /**
     * 从网络Url中下载文件
     * @param urlStr
     * @param fileName
     * @param savePath
     * @throws IOException
     */
    public static void  downLoadFromUrl(String urlStr, String fileName, String savePath) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(3*1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        //得到输入流
        InputStream inputStream = conn.getInputStream();
        //获取自己数组
        byte[] getData = readInputStream(inputStream);
        //文件保存位置
        fileName = new String((conn.getURL().getPath().substring(conn.getURL().getPath().lastIndexOf("/") + 1)).getBytes(), "UTF-8");
        long fileSize = Long.valueOf(conn.getHeaderField("Content-Length"));

        File saveDir = new File(savePath);
        String path = saveDir + File.separator + fileName;
        File file = new File(path);

        //如果传入文件size 则判断本地是否已经下载该文件且大小一致(则不下载直接返回)
        if(fileSize > 0){
            if(file.exists() && fileSize == file.length()){
                System.out.println(fileName + "已存在:大小" + fileSize);
                return;
            }
        }

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if(fos!=null){
            fos.close();
        }
        if(inputStream!=null){
            inputStream.close();
        }
    }

    /**
     * 从网络Url中下载文件
     * @param urlStr
     * @param fileName
     * @param savePath
     * @throws IOException
     */
    public static void  downLoadFromUrl(String urlStr, String fileName, String savePath, ProgressBar progressBar, Label proLabel, Double proSize) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(3*1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

        long fileSize = Long.valueOf(conn.getHeaderField("Content-Length"));
        //文件保存位置
        fileName = new String((conn.getURL().getPath().substring(conn.getURL().getPath().lastIndexOf("/") + 1)).getBytes(), "UTF-8");

        File saveDir = new File(savePath);
        String path = saveDir + File.separator + fileName;
        File file = new File(path);

        //判断文件size 本地是否已经下载该文件且大小一致(则不下载直接返回)
        if(fileSize > 0){
            if(file.exists() && fileSize == file.length()){
                Platform.runLater(() -> {
                    progressBar.setProgress(progressBar.getProgress() + proSize);
                    String[] split = proLabel.getText().split("/");
                    proLabel.setText(Integer.valueOf(split[0]) + 1 + "/" + split[1]);
                    if((Integer.valueOf(split[0]) + 1) == Integer.valueOf(split[1])){
                        Label resultLabel = MessageUtils.resultLabel;
                        if(MessageUtils.isOk()){
                            DownLoadUtils.isOpenLanauch(resultLabel);
                        }else{
                            MessageUtils.setOk();
                        }
                    }
                });
                MessageUtils.info("跳过已存在:" + fileName.substring(0, fileName.length() - 4));
                return;
            }else {
                MessageUtils.info("正在下载Mod:" + fileName.substring(0, fileName.length() - 4));
            }
        }

        //得到输入流
        InputStream inputStream = conn.getInputStream();
        //获取自己数组
        byte[] getData = readInputStream(inputStream);

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if(fos!=null){
            fos.close();
        }
        if(inputStream!=null){
            inputStream.close();
        }
        Platform.runLater(() -> {
            progressBar.setProgress(progressBar.getProgress() + proSize);
            String[] split = proLabel.getText().split("/");
            proLabel.setText(Integer.valueOf(split[0]) + 1 + "/" + split[1]);
            Label resultLabel = MessageUtils.resultLabel;
            if((Integer.valueOf(split[0]) + 1) == Integer.valueOf(split[1])){
                if(MessageUtils.isOk()){
                    DownLoadUtils.isOpenLanauch(resultLabel);
                }else{
                    MessageUtils.setOk();
                }
            }
        });
    }

    /**
     * 从输入流中获取字节数组
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);;
            MessageUtils.sizeAI.set(MessageUtils.sizeAI.get() + len);
        }
        bos.close();
        return bos.toByteArray();
    }

    /**
     * 从网络Url中下载文件到指定目录 通用方法
     * @param urlStr
     * @param savePath
     * @throws IOException
     */
    public static String downLoadFromUrl(String urlStr, String savePath) throws IOException {
        if(savePath == null || "".equals(savePath)){ savePath = getRootPath(); }
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(3*1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

        //url文件大小
        long fileSize = Long.valueOf(conn.getHeaderField("Content-Length"));
        //文件保存位置
        String fileName = new String((conn.getURL().getPath().substring(conn.getURL().getPath().lastIndexOf("/") + 1)).getBytes(), "UTF-8");

        File saveDir = new File(savePath);
        String path = saveDir + File.separator + fileName;
        File file = new File(path);

        if(file.length() == fileSize){
            return fileName;
        }

        //得到输入流
        InputStream inputStream = conn.getInputStream();
        //获取自己数组
        byte[] getData = readInputStream(inputStream);

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if(fos!=null){
            fos.close();
        }
        if(inputStream!=null){
            inputStream.close();
        }
        return fileName;
    }

    /**
     * 获取文件大小
     * @param file
     */
    public static long getFileSize(File file) {
        if (file.exists() && file.isFile()) {
            return file.length();
        }
        return 0L;
    }

    /**
     * packCode 获取 需下载JSON数据
     */
    public static List<DownLoadModel> getDownLoadPacks(String packCode) throws Exception {
        URL url = new URL("http://mc.dawnland.cn:8443/api/proxy/" + packCode);
        InputStream ins = url.openConnection().getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
        String str = reader.readLine();
        Object oStr = JSONObject.parseObject(str).get("proxies");
        List<DownLoadModel> list = JSONObject.parseArray(oStr.toString(), DownLoadModel.class);
        return list;
    }

    public static void downloadVersionJson(String mcVersion, String forgeVersion){
        String s = HttpUtils.get(MojangUtils.getJsonUrl(mcVersion));
        JSONObject jsonObject = JSONObject.parseObject(s);
        JSONArray libraries = (JSONArray) jsonObject.get("libraries");
        JSONObject addJson = new JSONObject();
        String forgeJson = "net.minecraftforge:forge:" + forgeVersion.replace("forge", mcVersion);
        addJson.put("name", forgeJson);
        addJson.put("url", "http://files.minecraftforge.net/maven/");
        libraries.add(addJson);
        String tempStr = packPath.substring(packPath.lastIndexOf("/") + 1);
        File f = new File(packPath + "/" + (".minecraft".equals(tempStr) ? "versions/" + mcVersion + "/" + mcVersion + ".json" : tempStr + ".json"));
        if(!f.isDirectory()) {
            File tempF = new File(f.getPath().substring(0, f.getPath().lastIndexOf("\\")));
            tempF.mkdirs();
            f = new File(packPath + "/" + (".minecraft".equals(tempStr) ? "versions/" + mcVersion + "/" + mcVersion + ".json" : tempStr + ".json"));
        }
        FileOutputStream fio = null;
        try {
            fio = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        PrintStream ps = new PrintStream(fio);
        ps.print(jsonObject.toJSONString());
        ps.close();
        try {
            fio.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void isOpenLanauch(Label resultLabel){
        JOptionPane.showMessageDialog(null, "打开启动器开始玩耍吧!", "安装完成", 1);
        CommonUtils.appExit();

//        resultLabel.setText("安装完成");
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.setTitle("安装完成");
//        alert.setHeaderText("");
//        alert.setContentText("是否打开启动器");
//        Optional result = alert.showAndWait();
//        if (result.get() == ButtonType.OK) {
//            /** 执行cmd命令打开启动器 */
//            try {
//                Desktop.getDesktop().open(new File(DownLoadUtils.getRootPath() + "/黎明大陆伪正版启动器.exe"));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            System.exit(0);
//        }
    }

}
