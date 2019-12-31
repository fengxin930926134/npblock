package com.np.block.core.model;

import lombok.Data;

/**
 * 用户队列信息
 *
 * @author fengxin
 */
@Data
public class QueueMessage {
    /**唯一标识个人 由客户端随机生成*/
    private String id;
    /**唯一标识队伍*/
    private String key;
    /**携带的信息*/
    private String msg;
    private String gameType;
}
