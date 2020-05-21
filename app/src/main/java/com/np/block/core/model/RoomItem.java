package com.np.block.core.model;

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

    public RoomItem(Integer roomId, String roomName, Integer roomType, String createUser) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomType = roomType;
        this.createUser = createUser;
    }

    public RoomItem() {

    }
}
