package com.np.block.core.model;

import org.litepal.crud.LitePalSupport;

import androidx.annotation.NonNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 俄罗斯方块的单元块
 * @author fengxin
 */
@Getter
@Setter
@ToString
public class UnitBlock extends LitePalSupport implements Cloneable {
    /**单元块的默认边长*/
    public static final int BLOCK_SIZE = 50;
    /**单元块的默认角度*/
    public static final int ANGLE = 8;
    /**保存到数据库的开关*/
    public static boolean isSave = false;
    /**单元块的颜色*/
    private int color;
    /**单元块的x坐标*/
    private int x;
    /**单元块的y坐标*/
    private int y;

    UnitBlock(int x, int y, int color) {
        this.x = x;
        this.y = y;
        this.color = color;
        if (isSave) {
            save();
        }
    }

    @NonNull
    @Override
    public UnitBlock clone() throws CloneNotSupportedException {
        return (UnitBlock) super.clone();
    }
}
