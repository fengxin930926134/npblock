package com.np.block.core.model;

import lombok.Data;
import java.util.Date;

/**
 * 对应后台users表
 * @author fengxin
 */
@Data
public class Users {
    private Integer id;
    private String name;
    private String phone;
    private String password;
    private String openId;
    private String gameName;
    private String token;
    private Long tokenTime;
    private Date createDate;
    private String headSculpture;
    /**
     * 1.男 2.女
     */
    private Integer sex;
    private Integer classicId;
    private Integer rushId;
    private Integer rankId;
    /** 用商品id做字符分割*/
    private Integer knapsack;
    /** 方块币钱包*/
    private Integer walletBlock;
    /** 钻石币钱包*/
    private Integer walletJewel;
    /**中间变量*/
    private Integer classicScore;
    private Integer rankScore;
    private Integer rushScore;
    private Integer rushPass;
    /**是否晋级赛*/
    private Boolean riseInRank;
    /**申请理由， 只在客户端存在*/
    private String reason;
}
