package cn.dawnland.packdownload.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Version {

    private Date releaseTime;
    private String id;
    private Date time;
    private String type;
    private String url;

}
