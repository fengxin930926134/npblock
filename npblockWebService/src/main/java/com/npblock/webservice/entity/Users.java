package com.npblock.webservice.entity;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name="users")
public class Users {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
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
    @Transient
    private Integer classicScore;
    @Transient
    private Integer rankScore;
    @Transient
    private Integer rushScore;
    @Transient
    private Integer rushPass;
    /**是否晋级赛*/
    @Transient
    private Boolean riseInRank;
}
