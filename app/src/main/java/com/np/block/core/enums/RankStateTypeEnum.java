package com.np.block.core.enums;

import lombok.Getter;

 /**
 * 排位状态类型
 * @author fengxin
 */
@Getter
public enum RankStateTypeEnum {
    /**游戏结束类型*/
    DEFAULT("DEFAULT", "正常状态"),
    PROMOTION_SUCCESS_TYPE("PROMOTION_SUCCESS_TYPE", "晋级失败"),
    PROMOTION_DEFEATED_TYPE("PROMOTION_DEFEATED_TYPE", "晋级成功"),
    GET_INTO_PROMOTION_TYPE("GET_INTO_PROMOTION_TYPE", "开启晋级赛");

    private final String code;
    private final String des;

     RankStateTypeEnum(String code, String des) {
        this.code = code;
        this.des = des;
    }

    public static RankStateTypeEnum getEnumByCode(String codeVal){
        for (RankStateTypeEnum e : values()){
            if (e.code.equalsIgnoreCase(codeVal)){
                return e;
            }
        }
        return DEFAULT;
    }
}