package configs;

import lombok.Data;

import java.util.Properties;

import static utils.Upgrader.urlEncodeChinese;

/**
 * @author Cap_Sub
 * @version 创建时间：19/02/29 11:29
 */
@Data
public class Config {

    public static float currentVersion = 1.23f;
    public static String batUrl = "https://dawnland.cn/update.bat";
    public static String versionUrl = "https://dawnland.cn/version.json";
    public static String exeUrl = urlEncodeChinese("https://dawnland.cn/Curse整合包下载器.exe");
    public static String string2download = urlEncodeChinese("https://dawnland.cn/Curse整合包下载器.exe");
    public static String mcjarStr = "该整合包核心要求如下: \n游戏版本: *mcVersion*\nforge版本: *forgeVersion*\n请在下载完成后使用启动器安装核心";
    public static Properties properties;

//    static {
//        init();
//    }
//
//    public static void init(){
//        if(properties == null){
////            file = new File("src/main/resources/config.properties");
//            File file = new File(Config.class.getResource("/config.properties").getFile());
//            InputStream in;
//            Properties properties = null;
//            try {
//                in = new FileInputStream(file);
//                properties = new Properties();
//                properties.load(in);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            try {
//                currentVersion = Float.valueOf(properties.getProperty("currentVersion"));
//            } catch (Exception e) {
//                currentVersion = 1.0f;
//            }
//            mcjarStr = properties.getProperty("mcjarStr");
//            batUrl = urlEncodeChinese(properties.getProperty("batUrl"));
//            versionUrl = urlEncodeChinese(properties.getProperty("versionUrl"));
//            exeUrl = urlEncodeChinese(properties.getProperty("exeUrl"));
//            string2download = urlEncodeChinese(properties.getProperty("string2download"));
//        }
//    }

}
