package cn.dawnland.packdownload.model;

public class ForgeVersion {

    private final static String forgeInstallBaseUrl = "http://files.minecraftforge.net/" +
            "maven/net/minecraftforge/" +
            "forge/{forgeVersion}/" +
            "{installVersion}-installer.jar";

    private String mcVersion;
    private String forgeVersion;

    public ForgeVersion(String mcVersion, String forgeVersion) {
        this.mcVersion = mcVersion;
        this.forgeVersion = forgeVersion;
    }

    public String getForgeInstallUrl(){
        String temp = forgeInstallBaseUrl.
                replaceFirst("\\{forgeVersion}",
                        mcVersion + forgeVersion.replace("forge", ""));
        temp = temp.replaceFirst("\\{installVersion}",
                "forge-" + mcVersion + forgeVersion.replace("forge", ""));
        return temp;
    }

}
