package com.np.block.core.enums;

import lombok.Getter;

/**
 * 商品购买类型
 * @author fengxin
 */
@Getter
public enum GoodsTypeEnum {
    /**商品购买类型*/
    DEFAULT("DEFAULT", "默认未知"),
    BLOCK_MONEY_TYPE("BLOCK_MONEY_TYPE", "方块币购买类型"),
    JEWEL_MONEY_TYPE("JEWEL_MONEY_TYPE", "彩钻购买类型");

    private final String code;
    private final String des;

    GoodsTypeEnum(String code, String des) {
        this.code = code;
        this.des = des;
    }

    public static GoodsTypeEnum getEnumByCode(String codeVal){
        for (GoodsTypeEnum e : values()){
            if (e.code.equalsIgnoreCase(codeVal)){
                return e;
            }
        }
        return DEFAULT;
    }
}
