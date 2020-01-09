package com.np.block.base;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import com.np.block.core.enums.TetrisTypeEnum;
import com.np.block.core.model.Tetris;
import com.np.block.core.model.UnitBlock;
import com.np.block.util.ConstUtils;
import com.np.block.util.TetrisControllerUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 俄罗斯方块视图 base
 * @author fengxin
 */
public abstract class BaseTetrisView extends View {
    /**
     * 游戏主界面开始的坐标
     */
    public static final int BEGIN_LEN_X = 5;
    public static final int BEGIN_LEN_Y = 5;
    /**
     * 行数和列数
     */
    public static final int ROW_NUM = 20;
    public static final int COLUMN_NUM = 10;
    /**墙宽度*/
    public static final int BOUND_WIDTH_OF_WALL = 3;
    /**方块宽度*/
    public int blockSize;
    /**游戏主界面结束的x坐标*/
    public int end_x_len;
    /**游戏主界面结束的y坐标*/
    public int end_y_len;
    /**俄罗斯方块*/
    public Tetris tetris = null;
    /**俄罗斯方块的单元块集合*/
    public List<UnitBlock> tetrisUnits = null;
    /**视图中全部已下落单元块集合*/
    public List<UnitBlock> allUnitBlock = new ArrayList<>();
    /**背景矩形模具集合*/
    private List<RectF> backgroundWall = new ArrayList<>();
    /**俄罗斯方块矩形模具集合*/
    private List<RectF> tetrisRectf = new ArrayList<>();
    /**所有完成下落方块矩形模具集合*/
    private List<RectF> allBlockRectf = new ArrayList<>();
    /**视图中全部已下落单元块集合长度*/
    private int allUnitBlockLength = allUnitBlock.size();
    /**背景墙画笔*/
    private static Paint paintWall = null;
    /**单元块画笔*/
    private static Paint paintBlock = null;
    /**保存每行网格中包含俄罗斯方块单元的个数*/
    private int[] blockRowNum = new int[ROW_NUM];

    public BaseTetrisView(Context context) {
        super(context, null);
    }

    public BaseTetrisView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 初始化属性
        blockSize = getBlockSize();
        end_x_len = BEGIN_LEN_X + (blockSize + BOUND_WIDTH_OF_WALL) * COLUMN_NUM + BOUND_WIDTH_OF_WALL;
        end_y_len = BEGIN_LEN_Y + (blockSize + BOUND_WIDTH_OF_WALL) * ROW_NUM + BOUND_WIDTH_OF_WALL;
        // 初始化背景墙笔
        if (paintWall == null) {
            paintWall = new Paint();
            // 设置画笔空心
            paintWall.setStyle(Paint.Style.STROKE);
            // 设置画笔颜色
            paintWall.setColor(Color.GRAY);
            // 设置线宽
            paintWall.setStrokeWidth(BOUND_WIDTH_OF_WALL);
        }
        // 初始化方块笔
        if (paintBlock == null) {
            paintBlock = new Paint();
            paintBlock.setColor(Color.parseColor("#FF6600"));
        }
        // 初始化背景墙 因为忽略墙 会多画一次 所以减一
        for (int i = BEGIN_LEN_X; i < end_x_len - blockSize; i += blockSize) {
            for (int j = BEGIN_LEN_Y; j < end_y_len - blockSize; j += blockSize) {
                backgroundWall.add(new RectF(i, j, i + blockSize, j + blockSize));
            }
        }
        // 每行网格中包含俄罗斯方块单元的个数全部初始化为0
        Arrays.fill(blockRowNum, 0);
        // 初始化俄罗斯方块
        tetris = new Tetris(BEGIN_LEN_X + COLUMN_NUM / 2 * blockSize, BEGIN_LEN_Y,
                TetrisTypeEnum.DEFAULT, -1, blockSize);
        tetrisUnits = tetris.getTetris();
        // 生成俄罗斯方块模具
        generateTetrisRectf();
    }

    /**
     * 设置视图所占宽高
     * @param widthMeasureSpec 父视图宽
     * @param heightMeasureSpec 父视图高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 重新计算高度
        int width = end_x_len - 5 * BEGIN_LEN_X - BOUND_WIDTH_OF_WALL;
        int height = end_y_len - BEGIN_LEN_Y - BOUND_WIDTH_OF_WALL;
        // 设置宽高
        setMeasuredDimension(width, height);
    }

    /**
     * SuppressLint ：循环new对象应该在初始化时进行
     *
     * @param canvas 画布
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画背景
        for (int i = 0; i < backgroundWall.size(); i++) {
            canvas.drawRoundRect(backgroundWall.get(i), UnitBlock.ANGLE, UnitBlock.ANGLE, paintWall);
        }
        // 设置绘制俄罗斯方块的颜色
        paintBlock.setColor(ConstUtils.COLOR[tetris.getColor()]);
        // 绘制俄罗斯方块
        for (int i = 0; i < tetrisRectf.size(); i++) {
            // 用事先设定好的背景画笔画俄罗斯单位方块
            canvas.drawRoundRect(tetrisRectf.get(i), UnitBlock.ANGLE, UnitBlock.ANGLE, paintBlock);
        }
        // 绘制所有方块
        for (int i = 0; i < allBlockRectf.size(); i++) {
            // 设置绘制俄罗斯方块的颜色 每一个单元块对应一个模具
            paintBlock.setColor(ConstUtils.COLOR[allUnitBlock.get(i).getColor()]);
            canvas.drawRoundRect(allBlockRectf.get(i), UnitBlock.ANGLE, UnitBlock.ANGLE, paintBlock);
        }
    }

    /**
     * 从子类获取方块宽高
     *
     * @return 方块宽高
     */
    public abstract int getBlockSize();

    /**
     * 从子类获取下一个俄罗斯方块
     *
     * @return 俄罗斯方块
     */
    public abstract Tetris getNextTetris();

    /**
     * 俄罗斯方块左移
     */
    public void toLeft() {
        if (TetrisControllerUtils.canMoveLeft(tetrisUnits, allUnitBlock)) {
            TetrisControllerUtils.toLeft(tetrisUnits);
            tetris.setX(tetris.getX() - blockSize);
            generateTetrisRectf();
        }
        invalidate();
    }

    /**
     * 俄罗斯方块右移
     */
    public void toRight() {
        if (TetrisControllerUtils.canMoveRight(tetrisUnits, allUnitBlock)) {
            TetrisControllerUtils.toRight(tetrisUnits);
            tetris.setX(tetris.getX() + blockSize);
            generateTetrisRectf();
        }
        invalidate();
    }

    /**
     * 俄罗斯方块下移
     *
     * @return boolean true表示游戏结束了
     */
    public boolean toDown() {
        if (TetrisControllerUtils.canMoveDown(tetrisUnits, allUnitBlock)) {
            TetrisControllerUtils.toDown(tetrisUnits);
            tetris.setY(tetris.getY() + blockSize);
            generateTetrisRectf();
            generateAllBlockRectf();
            invalidate();
            return false;
        }
        // 如果方块超出则结束游戏
        if (isGameOver(tetrisUnits)){
            invalidate();
            return true;
        }
        // 重新给每行方块数赋值为0 然后重新计算每行方块数
        Arrays.fill(blockRowNum, 0);
        // 把已经固定的俄罗斯方块加入总方块
        allUnitBlock.addAll(tetrisUnits);
        // 逆向遍历所有方块
        for (int i = allUnitBlock.size() - 1; i >= 0; i--) {
            // y是指方块是第几行
            int y = (allUnitBlock.get(i).getY() - BEGIN_LEN_Y) / blockSize;
            for (int j = blockRowNum.length; j > 0; j--) {
                if (y == j) {
                    blockRowNum[j - 1]++;
                }
            }
        }
        //判断是否有满足一行 满足则消除
        List<Integer> rows = blockCramRow();
        if (rows.size() > 0) {
            removeRowsBlock(rows);
        }
        // 所有已下落方块此时已经刷新 需要重新生成模具
        generateAllBlockRectf();
        // 生成新俄罗斯方块
        generateTetris();
        // 此时生成新方块了 需要重新刷新方块模具
        generateTetrisRectf();
        invalidate();
        return false;
    }

    /**
     * 俄罗斯方块旋转
     */
    public void toRotate() {
        TetrisControllerUtils.toRotate(tetris, allUnitBlock);
        generateTetrisRectf();
        invalidate();
    }

    /**
     * 消除塞满方块的所有行
     *
     * @param rows 指定删除行
     */
    public void removeRowsBlock(List<Integer> rows) {
        // 再次判断是否满足一行
        if (rows.size() > 0) {
            for (int row : rows) {
                // 再次判断是否满足一行
                if (blockRowNum[row - 1] == COLUMN_NUM) {
                    // 循环移除要消掉的行
                    TetrisControllerUtils.removeLine(allUnitBlock, row);
                    // 将标记变量还原
                    blockRowNum[row - 1] = 0;
                    // 消除的一行的上方的方块整体下移
                    TetrisControllerUtils.blockRowsToDown(allUnitBlock, row);
                }
            }
            // 对模具刷新
            generateAllBlockRectf();
        }
    }

    /**
     * 生成俄罗斯方块
     */
    private void generateTetris(){
        // 获得下一个俄罗斯方块
        Tetris nextTetris = getNextTetris();
        // 从下一个俄罗斯方块生成当前俄罗斯方块
        tetris = new Tetris(BEGIN_LEN_X + COLUMN_NUM / 2 * UnitBlock.BLOCK_SIZE, BEGIN_LEN_Y,
                TetrisTypeEnum.getEnumByCode(nextTetris.getTetrisType()),
                nextTetris.getColor(),
                UnitBlock.BLOCK_SIZE);
        // 获得单位方块集合
        tetrisUnits = tetris.getTetris();
    }

    /**
     * 判断方块满足一行的行数
     *
     * @return List<Integer>满足行数
     */
    private List<Integer> blockCramRow() {
        //塞满一行的行数
        List<Integer> rows = new ArrayList<>();
        for (int i = 0; i < blockRowNum.length; i++) {
            // 一行的方块数等于列数则满足一行
            if (blockRowNum[i] == COLUMN_NUM) {
                rows.add(i + 1);
            }
        }
        return rows;
    }

    /**
     * 判断游戏是否结束
     * @return true结束 false不结束
     */
    private boolean isGameOver(List<UnitBlock> tetrisUnits) {
        for (int i = 0; i < tetrisUnits.size(); i++) {
            if (tetrisUnits.get(i).getY() <= BEGIN_LEN_Y ){
                return true;
            }
        }
        return false;
    }

    /**
     * 生成俄罗斯方块模具
     */
    private void generateTetrisRectf(){
        if (tetrisRectf.size() > 0) {
            tetrisRectf.clear();
        }
        for (int i = 0; i < tetrisUnits.size(); i++) {
            // 获取每个单位方块的坐标
            int x = tetrisUnits.get(i).getX();
            int y = tetrisUnits.get(i).getY();
            // 设置俄罗斯方块单位的坐标矩形
            tetrisRectf.add(new RectF(x + BOUND_WIDTH_OF_WALL, y + BOUND_WIDTH_OF_WALL,
                    x + blockSize - BOUND_WIDTH_OF_WALL, y + blockSize - BOUND_WIDTH_OF_WALL));
        }
    }

    /**
     * 生成所有方块模具
     */
    private void generateAllBlockRectf(){
        // 判断已下落的方块是否改变了 不改变就不需要重新生成
        if (allUnitBlockLength != allUnitBlock.size()) {
            if (allBlockRectf.size() > 0) {
                allBlockRectf.clear();
            }
            for (int i = 0; i < allUnitBlock.size(); i++) {
                // 获取每个单位方块的坐标
                int x = allUnitBlock.get(i).getX();
                int y = allUnitBlock.get(i).getY();
                // 设置俄罗斯方块单位的坐标矩形
                allBlockRectf.add(new RectF(x + BOUND_WIDTH_OF_WALL, y + BOUND_WIDTH_OF_WALL,
                        x + blockSize - BOUND_WIDTH_OF_WALL, y + blockSize - BOUND_WIDTH_OF_WALL));
            }
            // 将新生成的长度保存
            allUnitBlockLength = allUnitBlock.size();
        }
    }
}
