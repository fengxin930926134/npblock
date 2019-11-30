package com.np.block.core.model;

import com.np.block.util.ConstUtils;
import com.np.block.util.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * 俄罗斯方块 7种类型方块
 * @author fengxin
 */
@Getter
@Setter
public class Tetris {
    /**俄罗斯方块类型总和*/
    private static final int TYPES = 7;
    /**俄罗斯方块的单位方块个数*/
    public static final int TETRIS_NUMBER = 4;
    /**要除的数*/
    public static final int DIVISOR = 2;
    /**俄罗斯方块种类*/
    private int blockType;
    /**俄罗斯方块颜色*/
    private int color;
    /**俄罗斯方块坐标*/
    private int x,  y;
    /**俄罗斯方块 由单元块组成*/
    private List<UnitBlock> tetris;

    public Tetris(int x, int y, int blockType,int color) {
        this.x = x;
        this.y = y;
        tetris = new ArrayList<>();
        if (blockType == 0) {
            // 随机生成一个俄罗斯方块类型
            this.blockType = RandomUtils.getInt(TYPES);
        }else {
            this.blockType = blockType;
        }
        if (color == -1) {
            // 随机生成一个颜色
            this.color = RandomUtils.getInt(ConstUtils.COLOR.length) - 1;
        }else {
            this.color = color;
        }
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
                for (int i = 0; i < TETRIS_NUMBER/DIVISOR; i++) {
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
                for (int i = 0; i < TETRIS_NUMBER/DIVISOR; i++) {
                    tetris.add(new UnitBlock(x - i * UnitBlock.BLOCK_SIZE, y, this.color));
                    tetris.add(new UnitBlock(x + i * UnitBlock.BLOCK_SIZE, y + UnitBlock.BLOCK_SIZE, this.color));
                }
                break;
            // 俄罗斯方块反Z类型
            case 7:
                for (int i = 0; i < TETRIS_NUMBER/DIVISOR; i++) {
                    tetris.add(new UnitBlock(x + (i - 1) * UnitBlock.BLOCK_SIZE, y, this.color));
                    tetris.add(new UnitBlock(x - (i + 1) * UnitBlock.BLOCK_SIZE, y + UnitBlock.BLOCK_SIZE, this.color));
                }
                break;
                default:
        }
    }
}
