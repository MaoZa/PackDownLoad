package cn.dawnland.packdownload.model;

import lombok.Data;

@Data
public abstract class DownLoadModel {

    //保存文件名
    protected String fileName;
    //保存路径
    protected String path;
    //下载url
    protected String url;
    //文件大小(字节)
    protected Long fileSize;
    //下载类型
    protected ModelTypeEnum type;

}
