package cn.dawnland.packdownload.types;

import lombok.Getter;

/**
 * @author Created by cap_sub@dawnland.cn
 */
@Getter
public enum DownloadStatusType {

    SUCCESS(0, "成功"),
    UNKNOWN_ERROR(-1, "未知错误"),
    FAILED(500, "下载失败"),
    TIMEOUT(408, "超时"),
    ALREADY_EXIST(1, "已存在"),
    CANCELED(2, "取消下载"),
    PAUSED(3,"暂停下载")
    ;

    DownloadStatusType(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private int code;
    private String msg;

}
