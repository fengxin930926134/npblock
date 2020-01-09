package com.np.block.core.model;

import com.np.block.core.enums.TetrisTypeEnum;
import com.np.block.util.ConstUtils;
import com.np.block.util.RandomUtils;
import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 俄罗斯方块 7种类型方块
 * @author fengxin
 */
@Getter
@Setter
@ToString
public class Tetris extends LitePalSupport {
    /**俄罗斯方块的单位方块个数*/
    public static final int TETRIS_NUMBER = 4;
    /**要除的数*/
    public static final int DIVISOR = TETRIS_NUMBER/2;
    /**保存到数据库的开关*/
    public static boolean isSave = false;
    /**俄罗斯方块种类*/
    private String tetrisType;
    /**俄罗斯方块颜色*/
    private int color;
    /**俄罗斯方块坐标*/
    private int x,  y;
    /**俄罗斯方块 由单元块组成*/
    private List<UnitBlock> tetris;

    /**
     * 连缀查询查询关联表数据
     *
     * @return 俄罗斯方块对应的阻碍单元方块
     */
    public List<UnitBlock> getAllUnitBlock() {
        return LitePal.where("tetris_id = ?", String.valueOf(this.getBaseObjId())).find(UnitBlock.class);
    }

    /**
     * 创建指定类型的俄罗斯方块
     *
     * @param x 整体坐标点x
     * @param y 整体坐标点y
     * @param tetrisType 俄罗斯方块类型
     * @param color 颜色下标 (-1随机生成颜色)
     */
    public Tetris(int x, int y, TetrisTypeEnum tetrisType, int color, int blockSize) {
        this.x = x;
        this.y = y;
        tetris = new ArrayList<>();
        if (tetrisType == TetrisTypeEnum.DEFAULT) {
            // 随机生成一个俄罗斯方块类型
            tetrisType = TetrisTypeEnum.randomTetrisType();
        }
        this.tetrisType = tetrisType.getCode();
        if (color == -1) {
            // 随机生成一个颜色
            this.color = RandomUtils.getInt(ConstUtils.COLOR.length) - 1;
        }else {
            this.color = color;
        }
        switch (tetrisType){
            // 俄罗斯方块横线类型
            case LINE_SHAPE:
                for (int i = 0; i < TETRIS_NUMBER; i++) {
                    tetris.add(new UnitBlock( x + (i - 2) *blockSize, y, this.color));
                }
                break;
            // 俄罗斯方块凸类型
            case CONVEX_SHAPE:
                tetris.add(new UnitBlock(x, y, this.color));
                for (int i = 0; i < TETRIS_NUMBER-1; i++) {
                    tetris.add(new UnitBlock(x + ( i - 1 ) * blockSize, y + blockSize ,this.color));
                }
                break;
            // 俄罗斯方块田类型
            case FIELD_SHAPE:
                for (int i = 0; i < TETRIS_NUMBER/DIVISOR; i++) {
                    tetris.add(new UnitBlock(x + ( i - 1 ) * blockSize, y, this.color));
                    tetris.add(new UnitBlock(x + ( i - 1 ) * blockSize, y + blockSize, this.color));
                }
                break;
            // 俄罗斯方块7类型
            case SEVEN_SHAPE:
                tetris.add(new UnitBlock(x - blockSize, y, this.color));
                for (int i = 0; i < TETRIS_NUMBER-1; i++) {
                    tetris.add(new UnitBlock(x, y + i * blockSize, this.color));
                }
                break;
            // 俄罗斯方块反7类型
            case BACK_SEVEN_SHAPE:
                tetris.add(new UnitBlock(x, y, this.color));
                for (int i = 0; i < TETRIS_NUMBER-1; i++) {
                    tetris.add(new UnitBlock(x - blockSize, y + i * blockSize, this.color));
                }
                break;
            // 俄罗斯方块Z类型
            case Z_SHAPE:
                for (int i = 0; i < TETRIS_NUMBER/DIVISOR; i++) {
                    tetris.add(new UnitBlock(x - i * blockSize, y, this.color));
                    tetris.add(new UnitBlock(x + i * blockSize, y + blockSize, this.color));
                }
                break;
            // 俄罗斯方块反Z类型
            case BACK_Z_SHAPE:
                for (int i = 0; i < TETRIS_NUMBER/DIVISOR; i++) {
                    tetris.add(new UnitBlock(x + (i - 1) * blockSize, y, this.color));
                    tetris.add(new UnitBlock(x - (i + 1) * blockSize, y + blockSize, this.color));
                }
                break;
        }
        if (isSave) {
            save();
        }
    }
}
