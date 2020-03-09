package com.np.block.core.enums;

import lombok.Getter;

/**
 * 性别枚举
 * @author fengxin
 */

@Getter
public enum SexTypeEnum {
    /**性别*/
    DEFAULT(0, "默认未知"),
    MALE(1, "男"),
    FEMALE(2, "女");

    private final Integer code;
    private final String des;

    SexTypeEnum(Integer code, String des) {
        this.code = code;
        this.des = des;
    }

    public static SexTypeEnum getEnumByCode(Integer codeVal){
        for (SexTypeEnum e : values()){
            if (e.code.equals(codeVal)){
                return e;
            }
        }
        return DEFAULT;
    }
}
