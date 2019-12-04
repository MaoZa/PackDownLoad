package cn.dawnland.packdownload.model;

public class ForgeVersion {

    private final static String forgeInstallBaseUrl = "http://files.minecraftforge.net/" +
            "maven/net/minecraftforge/" +
            "forge/{forgeVersion}/" +
            "{installVersion}-installer.jar";
    //https://files.minecraftforge.net/maven/net/minecraftforge
    // /forge/1.12.2-14.23.5.2847/forge-1.12.2-14.23.5.2847-installer.jar
    private final static String BMCLAPIForgeUniversalBaseUrl = "https://bmclapi2.bangbang93.com/forge/download?" +
            "mcversion={mcVersion}&version={forgeVersion}&category=universal&format=jar";
    private final static String BMCLAPIForgeInstallerBaseUrl = "https://bmclapi2.bangbang93.com/forge/download?" +
            "mcversion={mcVersion}&version={forgeVersion}&category=installer&format=jar";

    private String mcVersion;
    private String forgeVersion;

    public ForgeVersion(String mcVersion, String forgeVersion) {
        this.mcVersion = mcVersion;
        this.forgeVersion = forgeVersion;
    }

    public String getForgeInstallUrl(){
        String temp = "1.14.4".equals(mcVersion) || "1.13.2".equals(mcVersion) ?
                BMCLAPIForgeInstallerBaseUrl.replaceFirst("\\{mcVersion}", mcVersion) :
                BMCLAPIForgeUniversalBaseUrl.replaceFirst("\\{mcVersion}", mcVersion);
        temp = temp.replaceFirst("\\{forgeVersion}", forgeVersion);
        return temp;
    }
}
