package com.np.block.core.model;

/**
 * 俄罗斯方块的单元块
 */
public class UnitBlock {
    // 单元块的边长
    public static final int BLOCK_SIZE = 50;
    // 单元块的颜色
    private int color;
    // 单元块的x坐标
    private int x;
    // 单元块的y坐标
    private int y;

    public UnitBlock(){
    }

    public UnitBlock(int x, int y, int color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
