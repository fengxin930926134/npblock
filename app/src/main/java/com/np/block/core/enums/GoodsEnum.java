package com.np.block.core.enums;

import lombok.Getter;

/**
 * 商品类型
 * @author fengxin
 */
@Getter
public enum GoodsEnum {
    /**商品购买类型*/
    DEFAULT(0, "默认未知"),
    RENAME_CARD(1, "改名卡");

    private final Integer code;
    private final String des;

    GoodsEnum(Integer code, String des) {
        this.code = code;
        this.des = des;
    }

    public static GoodsEnum getEnumByCode(Integer codeVal){
        for (GoodsEnum e : values()){
            if (e.code.equals(codeVal)){
                return e;
            }
        }
        return DEFAULT;
    }
}
