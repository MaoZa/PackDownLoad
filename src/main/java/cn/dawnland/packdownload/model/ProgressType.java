package cn.dawnland.packdownload.model;

public enum ProgressType {

    DOWNLOAD("下载"), UNZIP("解压");

    private String name;

    ProgressType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
