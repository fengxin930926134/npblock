package com.np.block.util;

import com.np.block.base.BaseTetrisView;
import com.np.block.core.enums.TetrisTypeEnum;
import com.np.block.core.model.Tetris;
import com.np.block.core.model.UnitBlock;
import com.np.block.view.ClassicTetrisView;
import java.util.ArrayList;
import java.util.List;

/**
 * 控制俄罗斯方块的工具类
 * @author fengxin
 */
public class TetrisControllerUtils {

    /**
     * 判断方块单元和所有方块是否有重叠
     * @param allBlock 已经落下的全部方块
     * @param x 单位方块x坐标
     * @param y 单位方块y坐标
     * @return false不能移动; true能移动
     */
    private static boolean isOverlap(List<UnitBlock> allBlock, int x, int y, int blockSize){
        for (UnitBlock unitBlock : allBlock ) {
            if (Math.abs(x - unitBlock.getX()) < blockSize && Math.abs(y - unitBlock.getY()) < blockSize ){
                return true;
            }
        }
        return false;
    }

    /**
     * 判断方块是否可以向左移动
     * @param tetris 正在下落的方块
     * @param allBlock 已经落下的全部方块
     * @param blockSize 方块大小
     * @return false不能移动; true能移动
     */
    public static boolean canMoveLeft(List<UnitBlock> tetris, List<UnitBlock> allBlock, int blockSize){
        for (UnitBlock  unitBlock : tetris) {
            if (unitBlock.getX() - blockSize < BaseTetrisView.BEGIN_LEN_X){
                return false;
            }
            if ( isOverlap(allBlock, unitBlock.getX() - blockSize, unitBlock.getY(), blockSize)){
                return false;
            }
        }
        return true;
    }

    /**
     * 判断方块是否可以向右移动
     * @param tetris 正在下落的方块
     * @param allBlock 已经落下的全部方块
     * @param xEnd x轴结束时坐标
     * @param blockSize 方块大小
     * @return false不能移动; true能移动
     */
    public static boolean canMoveRight(List<UnitBlock> tetris, List<UnitBlock> allBlock, int xEnd, int blockSize){
        for (UnitBlock unitBlock : tetris) {
            if (unitBlock.getX() + blockSize > xEnd - blockSize){
                return false;
            }
            if (isOverlap(allBlock, unitBlock.getX() + blockSize, unitBlock.getY(), blockSize)){
                return false;
            }
        }
        return true;
    }

    /**
     * 判断方块是否可以向下移动
     * @param tetris 正在下落的方块
     * @param allBlock 已经落下的全部方块
     * @param yEnd y轴结束时坐标
     * @param blockSize 方块大小
     * @return false不能移动; true能移动
     */
    public static boolean canMoveDown(List<UnitBlock> tetris, List<UnitBlock> allBlock, int yEnd, int blockSize){
        for (UnitBlock unitBlock: tetris) {
            if (unitBlock.getY() + 2 * blockSize > yEnd) {
                return false;
            }
            if (isOverlap(allBlock, unitBlock.getX(), unitBlock.getY() + blockSize, blockSize)){
                return false;
            }
        }
        return true;
    }

    /**
     * 把当前方块向左移动一格
     * @param tetris 俄罗斯方块坐标
     * @param blockSize 方块大小
     */
    public static void toLeft(List<UnitBlock> tetris, int blockSize){
        for (UnitBlock unitBlock : tetris) {
            unitBlock.setX( unitBlock.getX() - blockSize);
        }
    }

    /**
     * 把当前方块向右移动一格
     * @param tetris 俄罗斯方块坐标
     * @param blockSize 方块大小
     */
    public static void toRight(List<UnitBlock> tetris, int blockSize){
        for (UnitBlock unitBlock : tetris) {
            unitBlock.setX( unitBlock.getX() + blockSize);
        }
    }

    /**
     * 把当前方块向下移动一格
     * @param tetris 俄罗斯方块坐标
     * @param blockSize 方块大小
     */
    public static void toDown(List<UnitBlock> tetris, int blockSize){
        for (UnitBlock unitBlock : tetris) {
            unitBlock.setY( unitBlock.getY() + blockSize);
        }
    }

    /**
     * 把当前方块向上移动一格
     * @param tetris 俄罗斯方块坐标
     * @param blockSize 方块大小
     */
    private static void toUp(List<UnitBlock> tetris, int blockSize){
        for (UnitBlock unitBlock : tetris) {
            unitBlock.setY( unitBlock.getY() - blockSize);
        }
    }

    /**
     * 把消掉的方块上方 向下移动一格
     * @param all 所有方块
     * @param row 被消掉的行
     * @param blockSize 方块大小
     */
    public static void blockRowsToDown(List<UnitBlock> all, int row, int blockSize){
        int y = row * blockSize + BaseTetrisView.BEGIN_LEN_Y;
        for (UnitBlock unitBlock : all) {
            if (unitBlock.getY() < y) {
                unitBlock.setY( unitBlock.getY() + blockSize);
            }
        }
    }

    /**
     * 按顺时针旋转
     *
     * @param tetris 俄罗斯方块
     * @param all 所有方块
     * @param xEnd x轴结束坐标
     * @param yEnd Y轴结束坐标
     * @param blockSize 方块大小
     */
    public static void toRotate(Tetris tetris, List<UnitBlock> all, int xEnd, int yEnd, int blockSize) {
        // 如果当前正在下落的方块为正方形,则不进行旋转
        if (tetris.getTetrisType().equals(TetrisTypeEnum.FIELD_SHAPE.getCode())) {
            return;
        }
        List<UnitBlock> tetrisCoord = tetris.getTetris();
        // 赋值一个实验变量
        List<UnitBlock> unitBlocksTest = new ArrayList<>();
        try {
            for (int i = 0; i < tetrisCoord.size(); i++) {
                unitBlocksTest.add(tetrisCoord.get(i).clone());
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        // 旋转实验变量
        for (UnitBlock unitBlock: unitBlocksTest) {
            int tx = unitBlock.getX();
            int ty = unitBlock.getY();
            unitBlock.setX(-(ty - tetris.getY()) + tetris.getX());
            unitBlock.setY(tx - tetris.getX() + tetris.getY());
        }
        // 把实验变量丢进去判断是否能解决bug  可以则返回true
        if (!routeTran(unitBlocksTest, all, xEnd, yEnd, blockSize)){
            // 不可以则不旋转了
            unitBlocksTest.clear();
            return;
        }
        unitBlocksTest.clear();
        // 旋转真实方块
        for (UnitBlock unitBlock: tetrisCoord) {
            int tx = unitBlock.getX();
            int ty = unitBlock.getY();
            unitBlock.setX(-(ty - tetris.getY()) + tetris.getX());
            unitBlock.setY(tx - tetris.getX() + tetris.getY());
        }
        // 丢进去变换一次防止出现bug
        routeTran(tetrisCoord, all, xEnd, yEnd, blockSize);
    }

    /**
     * 判断unitBlocks和总方块重叠或者越界可以修正否 可以则修正
     * @param unitBlocks 需要判断的方块
     * @param all 所有方块
     * @param xEnd x轴结束坐标
     * @param yEnd y轴结束坐标
     * @param blockSize 方块大小
     * @return 能修正返回true 不能修正则返回false
     */
    private static boolean routeTran(List<UnitBlock> unitBlocks, List<UnitBlock> all, int xEnd, int yEnd, int blockSize) {
        // 是否重叠所有方块中的一个
        boolean overlapAllBlock = false;
        // 左边超出
        boolean needLeftTran = false;
        // 右边超出
        boolean needRightTran = false;
        // 下边超出
        boolean needUpTran = false;
        // 循环判断是否超出边界以及重叠
        for (UnitBlock u : unitBlocks) {
            if (!needLeftTran && u.getX() < BaseTetrisView.BEGIN_LEN_X) {
                needLeftTran = true;
            }
            if (!needRightTran && u.getX() > xEnd - UnitBlock.BLOCK_SIZE) {
                needRightTran = true;
            }
            //超出下边界
            if (!needUpTran && u.getY() > yEnd - UnitBlock.BLOCK_SIZE) {
                needUpTran = true;
            }
            if (!overlapAllBlock && isOverlap(all, u.getX(), u.getY(), blockSize)){
                overlapAllBlock = true;
            }
        }
        // 重叠 又超出边界
        if (overlapAllBlock && (needLeftTran || needRightTran)) {
            return false;
        }
        // 如果超出左边界 开始平移
        while (needLeftTran) {
            if (canMoveRight(unitBlocks, all, xEnd, blockSize)){
                toRight(unitBlocks, blockSize);
            }else {
                return false;
            }
            needLeftTran = false;
            for (UnitBlock u : unitBlocks) {
                if (u.getX() < ClassicTetrisView.BEGIN_LEN_X) {
                    needLeftTran = true;
                    break;
                }
            }
        }
        // 如果超出右边界 开始平移
        while (needRightTran) {
            if (canMoveLeft(unitBlocks,all, blockSize)){
                toLeft(unitBlocks, blockSize);
            }else {
                return false;
            }
            needRightTran = false;
            for (UnitBlock u : unitBlocks) {
                if (u.getX() > xEnd - UnitBlock.BLOCK_SIZE) {
                    needRightTran = true;
                    break;
                }
            }
        }
        // 解决重叠
        if (overlapAllBlock){
            //判断漏出来的方块是左边还是右边
            int leftOrRight = isLeftOrRight(unitBlocks, all, blockSize);
            if (leftOrRight < 0) {
                for (int i = 0; i < Tetris.TETRIS_NUMBER -1; i++) {
                    toLeft(unitBlocks, blockSize);
                    overlapAllBlock = false;
                    for (UnitBlock u : unitBlocks) {
                        if (isOverlap(all, u.getX(), u.getY(), blockSize)){
                            overlapAllBlock = true;
                            break;
                        }
                    }
                    if (!overlapAllBlock) {
                        //验证是否超出边界
                        return isNotExceed(unitBlocks, xEnd);
                    }
                }
            }else if (leftOrRight > 0) {
                for (int i = 0; i < Tetris.TETRIS_NUMBER -1; i++) {
                    toRight(unitBlocks, blockSize);
                    overlapAllBlock = false;
                    for (UnitBlock u : unitBlocks) {
                        if (isOverlap(all, u.getX(), u.getY(), blockSize)){
                            overlapAllBlock = true;
                            break;
                        }
                    }
                    if (!overlapAllBlock) {
                        //验证是否超出边界
                        return isNotExceed(unitBlocks, xEnd);
                    }
                }
            }else {
                return false;
            }
        }
        // 如果不重叠 尝试解决下边界超出问题
        if (!overlapAllBlock) {
            if (needUpTran) {
                // 赋值一个实验变量
                for (int i = 0; true; i++) {
                    if (i >= Tetris.TETRIS_NUMBER) {
                        //已经上移3次 还是超出，则不能旋转
                        return false;
                    }
                    //向上移动一格
                    toUp(unitBlocks, blockSize);
                    needUpTran = false;
                    for (UnitBlock u : unitBlocks) {
                        //超出下边界
                        if (u.getY() > yEnd - UnitBlock.BLOCK_SIZE) {
                            needUpTran = true;
                            break;
                        }
                    }
                    if (!needUpTran) {
                        //不超出了
                        return true;
                    }
                }
            } else {
                return true;
            }
        } else {
            // 重叠false
            return false;
        }
    }

    /**
     * 判断重叠时漏出来的方块是左边还是右边
     * @param unitBlocks 俄罗斯方块的单位方块
     * @param all 所有的单位方块
     * @param blockSize 方块大小
     * @return 0无法解决， >0右，<0左
     */
    private static int isLeftOrRight(List<UnitBlock> unitBlocks, List<UnitBlock> all, int blockSize) {
        UnitBlock unitBlockOverLap = null;
        UnitBlock unitBlockNoOverLap = null;
        for (UnitBlock unitBlock: unitBlocks) {
            if (isOverlap(all, unitBlock.getX(), unitBlock.getY(), blockSize)) {
                unitBlockOverLap = unitBlock;
            } else {
                unitBlockNoOverLap = unitBlock;
            }
            if (unitBlockNoOverLap != null && unitBlockOverLap != null) {
                return unitBlockNoOverLap.getX() - unitBlockOverLap.getX();
            }
        }
        return 0;
    }

    /**
     * 判断是否没有超出左右边界
     *
     * @param unitBlocks 俄罗斯方块的单位方块
     * @return boolean
     */
    private static boolean isNotExceed(List<UnitBlock> unitBlocks, int xEnd) {
        // 循环判断是否超出边界
        for (UnitBlock u : unitBlocks) {
            if (u.getX() < xEnd) {
                return false;
            }
            if (u.getX() > xEnd - UnitBlock.BLOCK_SIZE) {
                return false;
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
            if ((allUnitBlock.get(i).getY() - ClassicTetrisView.BEGIN_LEN_Y) / UnitBlock.BLOCK_SIZE == y){
                allUnitBlock.remove(i);
            }
        }
    }
}

