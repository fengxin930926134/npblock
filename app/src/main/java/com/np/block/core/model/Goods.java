package com.np.block.core.model;

import lombok.Data;
import java.util.Date;

/**
 * 商品类
 * @author fengxin
 */
@Data
public class Goods {
    private Integer id;
    private String goodsName;
    /**商品头像*/
    private String goodsPicture;
    /**商品类型*/
    private String goodsType;
    private Integer goodsPrice;
    private Date createDate;
    private Integer recordId;
}
