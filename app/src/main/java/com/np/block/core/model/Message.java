package com.np.block.core.model;

import com.np.block.core.enums.MessageTypeEnum;
import lombok.Data;

/**
 * 用户队列信息
 *
 * @author fengxin
 */
@Data
public class Message {
    /**唯一标识个人 由客户端随机生成*/
    private String id;
    /**唯一标识队伍*/
    private String key;
    /**携带的信息*/
    private String msg;
    private String messageType;
    /**确认人数*/
    private Integer confirmNum;
    /**等待确认时间*/
    private Long matchWaitTime;
    /**游戏类型*/
    private String gameType;
    /**胜利时间*/
    private Long winTime;

    public MessageTypeEnum getMessageType() {
        return MessageTypeEnum.getEnumByCode(messageType);
    }

    public void setMessageType(MessageTypeEnum messageType) {
        this.messageType = messageType.getCode();
    }
}
