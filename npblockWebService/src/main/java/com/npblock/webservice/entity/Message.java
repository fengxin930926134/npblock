package com.npblock.webservice.entity;

import com.npblock.webservice.entity.enums.MessageTypeEnum;
import lombok.Data;

import java.net.SocketAddress;

/**
 * 用户队列信息
 *
 * @author fengxin
 */
@Data
public class Message {
    /**唯一标识个人 userId*/
    private String id;
    /**唯一标识队伍*/
    private String key;
    /**携带的信息*/
    private String msg;
    private String messageType;
    /**端口号和ip*/
    private SocketAddress address;
    /**确认人数*/
    private Integer confirmNum;
    /**等待确认时间*/
    private Long matchWaitTime;
    /**胜利时间*/
    private Long winTime;
    /**队伍Id*/
    private Integer groupId;

    public MessageTypeEnum getMessageType() {
        return MessageTypeEnum.getEnumByCode(messageType);
    }

    public void setMessageType(MessageTypeEnum messageType) {
        this.messageType = messageType.getCode();
    }
}

