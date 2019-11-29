package com.np.block.util;

import com.np.block.core.model.Tetris;
import com.np.block.core.model.UnitBlock;
import com.np.block.view.TetrisView;
import java.util.ArrayList;
import java.util.List;

/**
 * 控制俄罗斯方块的工具类
 * @author fengxin
 */
public class tetrisControllerUtils {

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
     * @param tetris 俄罗斯方块坐标
     */
    public static void toLeft(List<UnitBlock> tetris){
        for (UnitBlock unitBlock : tetris) {
            unitBlock.setX( unitBlock.getX() - UnitBlock.BLOCK_SIZE);
        }
    }

    /**
     * 把当前方块向右移动一格
     * @param tetris 俄罗斯方块坐标
     */
    public static void toRight(List<UnitBlock> tetris){
        for (UnitBlock unitBlock : tetris) {
            unitBlock.setX( unitBlock.getX() + UnitBlock.BLOCK_SIZE);
        }
    }

    /**
     * 把当前方块向右下移动一格
     * @param tetris 俄罗斯方块坐标
     */
    public static void toDown(List<UnitBlock> tetris){
        for (UnitBlock unitBlock : tetris) {
            unitBlock.setY( unitBlock.getY() + UnitBlock.BLOCK_SIZE);
        }
    }

    /**
     * 把消掉的方块上方 向下移动一格
     * @param all 所有方块
     * @param row 被消掉的行
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
     * @param tetris 俄罗斯方块
     */
    public static void toRotate(Tetris tetris, List<UnitBlock> all) {
        // 如果当前正在下落的方块为正方形,则不进行旋转
        if (tetris.getBlockType() == 3) {
            return;
        }
        List<UnitBlock> tetrisCoord = tetris.getTetris();
        // 赋值一个实验变量
        List<UnitBlock> unitBlocksTest = new ArrayList<>();
        for (int i = 0; i < tetrisCoord.size(); i++) {
            UnitBlock unitBlock = tetrisCoord.get(i);
            unitBlocksTest.add(new UnitBlock(unitBlock.getX(),unitBlock.getY(),unitBlock.getColor()));
        }
        // 旋转实验变量
        for (UnitBlock unitBlock: unitBlocksTest) {
            int tx = unitBlock.getX();
            int ty = unitBlock.getY();
            unitBlock.setX(-(ty - tetris.getY()) + tetris.getX());
            unitBlock.setY(tx - tetris.getX() + tetris.getY());
        }
        // 把实验变量丢进去判断是否能解决bug  可以则返回true
        if (!routeTran(unitBlocksTest,all)){
            // 不可以则不旋转了
            return;
        }
        // 旋转真实方块
        for (UnitBlock unitBlock: tetrisCoord) {
            int tx = unitBlock.getX();
            int ty = unitBlock.getY();
            unitBlock.setX(-(ty - tetris.getY()) + tetris.getX());
            unitBlock.setY(tx - tetris.getX() + tetris.getY());
        }
        // 丢进去变换一次防止出现bug
        routeTran(tetrisCoord,all);
    }

    /**
     * 判断unitBlocks和总方块重叠或者越界可以修正否 可以则修正
     * @param unitBlocks 需要判断的方块
     * @param all 所有方块
     * @return 能修正返回true 不能修正则返回false
     */
    private static boolean routeTran(List<UnitBlock> unitBlocks, List<UnitBlock> all) {
        // 是否重叠所有方块中的一个
        boolean overlapAllBlock = false;
        // 左边超出
        boolean needLeftTran = false;
        //右边超出
        boolean needRightTran = false;
        // 循环判断是否超出边界
        for (UnitBlock u : unitBlocks) {
            if (u.getX() < TetrisView.BEGIN_LEN_X ) {
                needLeftTran = true;
                break;
            }
            if (u.getX() > TetrisView.end_x_len - UnitBlock.BLOCK_SIZE) {
                needRightTran = true;
                break;
            }
            for (UnitBlock allU : all) {
                // 满足则重叠
                if (allU.getX() == u.getX() && allU.getY() == u.getY()) {
                    overlapAllBlock = true;
                    break;
                }
            }
        }
        // 重叠 又超出边界
        if (overlapAllBlock && (needLeftTran || needRightTran))
            return false;
        // 如果超出左边界 开始平移
        while (needLeftTran) {
            if (canMoveRight(unitBlocks,all)){
                toRight(unitBlocks);
            }else {
                return false;
            }
            needLeftTran = false;
            for (UnitBlock u : unitBlocks) {
                if (u.getX() < TetrisView.BEGIN_LEN_X) {
                    needLeftTran = true;
                    break;
                }
            }
        }
        // 如果超出右边界 开始平移
        while (needRightTran) {
            if (canMoveLeft(unitBlocks,all)){
                toLeft(unitBlocks);
            }else {
                return false;
            }
            needRightTran = false;
            for (UnitBlock u : unitBlocks) {
                if (u.getX() > TetrisView.end_x_len - UnitBlock.BLOCK_SIZE) {
                    needRightTran = true;
                    break;
                }
            }
        }
        // 解决重叠
        if (overlapAllBlock){
            for (int i = 0; i < Tetris.TETRIS_NUMBER -1; i++) {
                toLeft(unitBlocks);
                overlapAllBlock = false;
                for (UnitBlock u : unitBlocks) {
                    for (UnitBlock allU : all) {
                        // 满足则重叠
                        if (allU.getX() == u.getX() && allU.getY() == u.getY()) {
                            overlapAllBlock = true;
                            break;
                        }
                    }
                    if (overlapAllBlock)
                        break;
                }
                if (!overlapAllBlock) {
                    return true;
                }
            }

            for (int i = 0; i < 2*(Tetris.TETRIS_NUMBER -1); i++) {
                toRight(unitBlocks);
                overlapAllBlock = false;
                for (UnitBlock u : unitBlocks) {
                    for (UnitBlock allU : all) {
                        // 满足则重叠
                        if (allU.getX() == u.getX() && allU.getY() == u.getY()) {
                            overlapAllBlock = true;
                            break;
                        }
                    }
                    if (overlapAllBlock)
                        break;
                }
                if (!overlapAllBlock) {
                    return true;
                }
            }
        }
        return true;
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

