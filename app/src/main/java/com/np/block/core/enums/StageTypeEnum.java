package com.np.block.core.enums;

import lombok.Getter;

/**
 * 关卡类型
 * @author fengxin
 */
@Getter
public enum StageTypeEnum {
    /**闯关关卡类型*/
    FIRST_PASS("FIRST_PASS", "第一关");

    private final String code;
    private final String des;

    StageTypeEnum(String code, String des) {
        this.code = code;
        this.des = des;
    }
}
