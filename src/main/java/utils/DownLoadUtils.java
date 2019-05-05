package utils;

import com.alibaba.fastjson.JSONObject;
import model.DownLoadModel;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class DownLoadUtils {

    private static String rootPath = DownLoadUtils.getRootPath() + "";

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
        //如果传入文件size 则判断本地是否已经下载该文件且大小一致(则不下载直接返回成功)
        if(size != null && size > 0){
            File localFile = new File(path + "/" + fileName);
            if(localFile.exists() && size.equals(localFile.length())){
                return true;
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
        return System.getProperty("user.dir") + "/.minecraft";
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
        File saveDir = new File(savePath);
        File file = new File(saveDir+File.separator+fileName);
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
     * 从输入流中获取字节数组
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static  byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
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

}
