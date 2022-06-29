package com.npblock.webservice.entity.enums;

import lombok.Getter;

/**
 * 游戏类型
 * @author fengxin
 */
@Getter
public enum GameTypeEnum {
    /**游戏类型*/
    DEFAULT("DEFAULT", "默认未知"),
    SINGLE_PLAYER_GAME("SINGLE_PLAYER_GAME", "单人对战类型"),
    TWO_PLAYER_GAME("TWO_PLAYER_GAME", "双人对战类型");

    private final String code;
    private final String des;

    GameTypeEnum(String code, String des) {
        this.code = code;
        this.des = des;
    }

    public static GameTypeEnum getEnumByCode(String codeVal){
        for (GameTypeEnum e : values()){
            if (e.code.equalsIgnoreCase(codeVal)){
                return e;
            }
        }
        return DEFAULT;
    }
}
