package com.np.block.core.enums;

import com.np.block.util.RandomUtils;
import lombok.Getter;

/**
 * 俄罗斯方块类型
 */
@Getter
public enum TetrisTypeEnum {
    DEFAULT("DEFAULT", "随机类型"),
    LINE_SHAPE("LINE_SHAPE", "横线类型"),
    CONVEX_SHAPE("CONVEX_SHAPE", "凸类型"),
    FIELD_SHAPE("FIELD_SHAPE","田类型"),
    SEVEN_SHAPE("SEVEN_SHAPE", "7类型"),
    BACK_SEVEN_SHAPE("BACK_SEVEN_SHAPE", "反7类型"),
    Z_SHAPE("Z_SHAPE", "z类型"),
    BACK_Z_SHAPE("BACK_Z_SHAPE", "反z类型");

    private final String code;
    private final String des;

    TetrisTypeEnum(String code, String des) {
        this.code = code;
        this.des = des;
    }

    /**
     * 随机取一个除随机类型的俄罗斯方块类型
     *
     * @return TetrisTypeEnum 除随机类型外
     */
    public static TetrisTypeEnum randomTetrisType(){
        return values()[RandomUtils.getInt(values().length - 1)];
    }

    public static TetrisTypeEnum getEnumByCode(String codeVal){
        for (TetrisTypeEnum e : values()){
            if (e.code.equalsIgnoreCase(codeVal)){
                return e;
            }
        }
        return DEFAULT;
    }
}
