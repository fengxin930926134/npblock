package com.np.block.util;

import com.np.block.core.model.Tetris;
import com.np.block.core.model.UnitBlock;
import com.np.block.view.TetrisView;

import java.util.List;

/**
 * 控制俄罗斯方块的工具类
 */
public class ControlTetrisUtils {

    /**
     * 判断方块单元和所有方块是否有重叠
     * @param allBlock 已经落下的全部方块
     * @param x 单位方块x坐标
     * @param y 单位方块y坐标
     * @return false不能移动; true能移动
     */
    private  static boolean isOverlap(List<UnitBlock> allBlock, int x, int y){
        for (UnitBlock unitBlock : allBlock ) {
            if (Math.abs(x - unitBlock.getX()) < UnitBlock.BLOCK_SIZE && Math.abs(y - unitBlock.getY()) < UnitBlock.BLOCK_SIZE ){
                return true;
            }
        }
        return false;
    }

    /**
     * 判断方块是否可以向左移动
     * @param tetris 正在下落的方块
     * @param allBlock 已经落下的全部方块
     * @return false不能移动; true能移动
     */
    public static boolean canMoveLeft(List<UnitBlock> tetris, List<UnitBlock> allBlock){
        for (UnitBlock  unitBlock : tetris) {
            if (unitBlock.getX() - UnitBlock.BLOCK_SIZE < TetrisView.BEGIN_LEN_X){
                return false;
            }
            if ( isOverlap(allBlock, unitBlock.getX() - UnitBlock.BLOCK_SIZE, unitBlock.getY() )){
                return false;
            }
        }
        return true;
    }

    /**
     * 判断方块是否可以向右移动
     * @param tetris 正在下落的方块
     * @param allBlock 已经落下的全部方块
     * @return false不能移动; true能移动
     */
    public static boolean canMoveRight(List<UnitBlock> tetris, List<UnitBlock> allBlock){
        for (UnitBlock unitBlock : tetris) {
            if (unitBlock.getX() + UnitBlock.BLOCK_SIZE > TetrisView.end_x_len - UnitBlock.BLOCK_SIZE){
                return false;
            }
            if (isOverlap(allBlock, unitBlock.getX() + UnitBlock.BLOCK_SIZE, unitBlock.getY())){
                return false;
            }
        }
        return true;
    }

    /**
     * 判断方块是否可以向下移动
     * @param tetris 正在下落的方块
     * @param allBlock 已经落下的全部方块
     * @return false不能移动; true能移动
     */
    public static boolean canMoveDown(List<UnitBlock> tetris, List<UnitBlock> allBlock){
        for (UnitBlock unitBlock: tetris) {
            if (unitBlock.getY() + 2 * UnitBlock.BLOCK_SIZE > TetrisView.end_y_len) {
                return false;
            }
            if (isOverlap(allBlock, unitBlock.getX(), unitBlock.getY() + UnitBlock.BLOCK_SIZE)){
                return false;
            }
        }
        return true;
    }


    /**
     * 把当前方块向左移动一格
     * @param tetris
     */
    public static void toLeft(List<UnitBlock> tetris){
        for (UnitBlock unitBlock : tetris) {
            unitBlock.setX( unitBlock.getX() - UnitBlock.BLOCK_SIZE);
        }
    }

    /**
     * 把当前方块向右移动一格
     * @param tetris
     */
    public static void toRight(List<UnitBlock> tetris){
        for (UnitBlock unitBlock : tetris) {
            unitBlock.setX( unitBlock.getX() + UnitBlock.BLOCK_SIZE);
        }
    }

    /**
     * 把当前方块向右下移动一格
     * @param tetris
     */
    public static void toDown(List<UnitBlock> tetris){
        for (UnitBlock unitBlock : tetris) {
            unitBlock.setY( unitBlock.getY() + UnitBlock.BLOCK_SIZE);
        }
    }

    /**
     * 把消掉的方块上方 向下移动一格
     * @param all
     */
    public static void rowAboveToDown(List<UnitBlock> all,int row){
        int y = row * UnitBlock.BLOCK_SIZE + TetrisView.BEGIN_LEN_Y;
        for (UnitBlock unitBlock : all) {
            if (unitBlock.getY() < y) {
                unitBlock.setY( unitBlock.getY() + UnitBlock.BLOCK_SIZE);
            }
        }
    }

    /**
     * 按顺时针旋转
     */
    public static void toRotate(Tetris tetris) {
        List<UnitBlock> tetrisCoord = tetris.getTetris();
        if (tetris.getBlockType() == 3) {// 如果当前正在下落的方块为正方形,则不进行旋转
            return;
        }
        // 尚未补全功能： 检测旋转的时候会不会超出范围 如果超出则平移之后再旋转
        for (UnitBlock unitBlock: tetrisCoord) {
            int tx = unitBlock.getX();
            int ty = unitBlock.getY();
            unitBlock.setX(-(ty - tetris.getY()) + tetris.getX());
            unitBlock.setY(tx - tetris.getX() + tetris.getY());
        }
    }

    /**
     * 删除所有在第y行的单位方块
     * @param allUnitBlock 所有的单位方块
     * @param y y行
     */
    public static void removeLine(List<UnitBlock> allUnitBlock, int y ){
        for (int i = allUnitBlock.size() - 1; i >= 0; i--) {
            if ((allUnitBlock.get(i).getY() - TetrisView.BEGIN_LEN_Y) / 50 == y){
                allUnitBlock.remove(i);
            }
        }
    }
}
