package model;

public enum ModelTypeEnum {

    MOD(1, "mod"),
    CONFIG(2, "config"),
    VERSION(3, "version"),
    SCRIPT(4, "script");

    private Integer code;
    private String name;

    ModelTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
