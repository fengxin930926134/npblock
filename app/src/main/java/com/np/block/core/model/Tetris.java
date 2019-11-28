package com.np.block.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 俄罗斯方块
 */
public class Tetris {
    // 俄罗斯方块类型总和
    private static final int TYPES = 7;
    // 俄罗斯方块的单位方块个数
    private static final int TETRIS_NUMBER = 4;
    // 方块下落的速度
    public static final int SPEED = 1000;
    // 俄罗斯方块种类
    private int blockType;
    //  俄罗斯方块颜色
    private int color;
    //  俄罗斯方块坐标
    private int x,  y;
    // 俄罗斯方块
    private List<UnitBlock> tetris;

    public Tetris() {
    }

    public Tetris(int x, int y, int blockType,int color) {
        this.x = x;
        this.y = y;
        tetris = new ArrayList<>();
        if (blockType == 0) {
            // 随机生成一个俄罗斯方块类型
            this.blockType = (int) (Math.random() * TYPES) + 1;
        }else
            this.blockType = blockType;
        if (color == 0) {
            // 随机生成一个颜色
            this.color = (int) (Math.random() * 4) + 1;
        }else
            this.color = color;
        switch (this.blockType){
            // 俄罗斯方块横线类型
            case 1:
                for (int i = 0; i < TETRIS_NUMBER; i++) {
                    tetris.add(new UnitBlock( x + (i - 2) *UnitBlock.BLOCK_SIZE, y, this.color));
                }
                break;
            // 俄罗斯方块凸类型
            case 2:
                tetris.add(new UnitBlock(x, y, this.color));
                for (int i = 0; i < TETRIS_NUMBER-1; i++) {
                    tetris.add(new UnitBlock(x + ( i - 1 ) * UnitBlock.BLOCK_SIZE, y + UnitBlock.BLOCK_SIZE ,this.color));
                }
                break;
            // 俄罗斯方块田类型
            case 3:
                for (int i = 0; i < TETRIS_NUMBER/2; i++) {
                    tetris.add(new UnitBlock(x + ( i - 1 ) * UnitBlock.BLOCK_SIZE, y, this.color));
                    tetris.add(new UnitBlock(x + ( i - 1 ) * UnitBlock.BLOCK_SIZE, y + UnitBlock.BLOCK_SIZE, this.color));
                }
                break;
            // 俄罗斯方块7类型
            case 4:
                tetris.add(new UnitBlock(x - UnitBlock.BLOCK_SIZE, y, this.color));
                for (int i = 0; i < TETRIS_NUMBER-1; i++) {
                    tetris.add(new UnitBlock(x, y + i * UnitBlock.BLOCK_SIZE, this.color));
                }
                break;
            // 俄罗斯方块反7类型
            case 5:
                tetris.add(new UnitBlock(x, y, this.color));
                for (int i = 0; i < TETRIS_NUMBER-1; i++) {
                    tetris.add(new UnitBlock(x - UnitBlock.BLOCK_SIZE, y + i * UnitBlock.BLOCK_SIZE, this.color));
                }
                break;
            // 俄罗斯方块Z类型
            case 6:
                for (int i = 0; i < TETRIS_NUMBER/2; i++) {
                    tetris.add(new UnitBlock(x - i * UnitBlock.BLOCK_SIZE, y, this.color));
                    tetris.add(new UnitBlock(x + i * UnitBlock.BLOCK_SIZE, y + UnitBlock.BLOCK_SIZE, this.color));
                }
                break;
            // 俄罗斯方块反Z类型
            case 7:
                for (int i = 0; i < TETRIS_NUMBER/2; i++) {
                    tetris.add(new UnitBlock(x + (i - 1) * UnitBlock.BLOCK_SIZE, y, this.color));
                    tetris.add(new UnitBlock(x - (i + 1) * UnitBlock.BLOCK_SIZE, y + UnitBlock.BLOCK_SIZE, this.color));
                }
                break;
        }
    }

    public List<UnitBlock> getTetris() {
        return tetris;
    }

    public void setTetris(List<UnitBlock> tetris) {
        this.tetris = tetris;
    }

    public int getBlockType() {
        return blockType;
    }

    public void setBlockType(int blockType) {
        this.blockType = blockType;
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
