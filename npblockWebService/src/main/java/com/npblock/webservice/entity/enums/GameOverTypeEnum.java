package com.npblock.webservice.entity.enums;

import lombok.Getter;

/**
 * 游戏结束类型
 * @author fengxin
 */
@Getter
public enum GameOverTypeEnum {
    /**游戏结束类型*/
    DEFAULT("DEFAULT", "默认未知"),
    GAME_OVER_WIN_TYPE("GAME_OVER_WIN_TYPE", "游戏胜利类型"),
    GAME_OVER_LOSE_TYPE("GAME_OVER_LOSE_TYPE", "游戏失败类型"),
    GAME_OVER_ESCAPE_TYPE("GAME_OVER_ESCAPE_TYPE", "敌人逃跑类型");

    private final String code;
    private final String des;

    GameOverTypeEnum(String code, String des) {
        this.code = code;
        this.des = des;
    }

    public static GameOverTypeEnum getEnumByCode(String codeVal){
        for (GameOverTypeEnum e : values()){
            if (e.code.equalsIgnoreCase(codeVal)){
                return e;
            }
        }
        return DEFAULT;
    }
}
