package com.np.block.core.enums;

import lombok.Getter;

/**
 * UDP消息类型
 *
 * @author fengxin
 */
@Getter
public enum MessageTypeEnum {
    /**UDP消息类型*/
    DEFAULT("DEFAULT", "默认未知"),
    GAME_MESSAGE_TYPE("GAME_MESSAGE_TYPE", "游戏消息类型"),
    GAME_WIN_MESSAGE_TYPE("GAME_WIN_MESSAGE_TYPE", "游戏胜利消息类型"),
    GAME_DEFEAT_MESSAGE_TYPE("GAME_DEFEAT_MESSAGE_TYPE", "游戏失败消息类型"),
    SEND_KEY_TYPE("SEND_KEY_TYPE", "发送key类型"),
    ADD_MATCH_SUCCESS_TYPE("ADD_MATCH_SUCCESS_TYPE", "加入匹配成功类型"),
    MATCH_MESSAGE_TYPE("MATCH_MESSAGE_TYPE", "匹配消息类型");

    private final String code;
    private final String des;

    MessageTypeEnum(String code, String des) {
        this.code = code;
        this.des = des;
    }

    public static MessageTypeEnum getEnumByCode(String codeVal){
        for (MessageTypeEnum e : values()){
            if (e.code.equalsIgnoreCase(codeVal)){
                return e;
            }
        }
        return DEFAULT;
    }
}
