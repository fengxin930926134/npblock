package com.npblock.webservice.entity;

import lombok.Data;

/**
 * 房间item
 *
 * @author fengxin
 */
@Data
public class RoomItem {
    /**房间号*/
    private Integer roomId;
    private String roomName;
    /**1 双排 2 单人对战*/
    private Integer roomType;
    private String createUser;
}
