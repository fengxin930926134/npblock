package com.np.block.core.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 俄罗斯方块的单元块
 */
@Getter
@Setter
public class UnitBlock {
    // 单元块的边长
    public static final int BLOCK_SIZE = 50;
    // 单元块的颜色
    private int color;
    // 单元块的x坐标
    private int x;
    // 单元块的y坐标
    private int y;

    public UnitBlock(int x, int y, int color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }
}
