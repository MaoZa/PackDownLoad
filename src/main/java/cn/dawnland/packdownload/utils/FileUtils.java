package cn.dawnland.packdownload.utils;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileUtils {

    /**
     * 读取json文件并且转换成字符串
     * @param pactFile 文件的路径
     * @return
     * @throws IOException
     */
    public static String readJsonData(String pactFile){
        StringBuffer sb = new StringBuffer();
        File myFile = new File(pactFile);
        if (!myFile.exists()) {
            System.err.println("Can't Find " + pactFile);
        }
        try {
            FileInputStream fis = new FileInputStream(pactFile);
            InputStreamReader inputStreamReader = new InputStreamReader(fis, "UTF-8");
            BufferedReader in  = new BufferedReader(inputStreamReader);

            String str;
            while ((str = in.readLine()) != null) {
                sb.append(str);
            }
            in.close();
        } catch (IOException e) {
            MessageUtils.error(e);
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 删除临时文件
     */
    public static void deleteTmpFile(){
        String[] notTempFiles = {".minecraft", "黎明大陆伪正版启动器.exe", "Curse整合包下载器.exe", "更新日志.txt", "hmcl.json", "下载失败的MOD.txt"};
        List<String> notTempFileList = new ArrayList<>(Arrays.asList(notTempFiles));
        if(ManagementFactory.getRuntimeMXBean().getInputArguments().contains("-Xdebug")){
            String[] strs = {".git", ".idea", "src", "target", "PackDownLoad.iml", "pom.xml", "README.md"};
            notTempFileList.addAll(Arrays.asList(strs));
        }
        File tempFilePath = new File(DownLoadUtils.getRootPath());
        File[] files = tempFilePath.listFiles();
        for (File f : files) {
            if(!notTempFileList.contains(f.getName())){
                if(f.isDirectory()){
                    for (File file : f.listFiles()) {
                        file.delete();
                    }
                    f.delete();
                }else {
                    f.delete();
                }
            }
        }
    }

}
