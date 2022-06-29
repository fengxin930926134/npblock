package com.npblock.webservice.entity;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name="goods")
public class Goods {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    private String goodsName;
    /**商品头像*/
    private String goodsPicture;
    /**商品类型*/
    private String goodsType;
    private Integer goodsPrice;
    private Date createDate;
    /**记录id*/
    @Transient
    private Integer recordId;
}
