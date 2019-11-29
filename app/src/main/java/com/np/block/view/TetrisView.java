package com.np.block.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import com.np.block.activity.ClassicBlockActivity;
import com.np.block.core.model.Tetris;
import com.np.block.core.model.UnitBlock;
import com.np.block.util.ConstUtils;
import com.np.block.util.tetrisControllerUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 俄罗斯方块的视图
 * @author fengxin
 */
public class TetrisView extends View {
    // 游戏主界面开始的坐标
    public static final int BEGIN_LEN_X = 5;
    public static final int BEGIN_LEN_Y = 5;
    // 行数和列数
    private static final int ROW_NUM = 20;
    private static final int COLUMN_NUM = 10;
    // 墙宽度
    public static final int BOUND_WIDTH_OF_WALL = 3;
    // 游戏主界面结束的x坐标
    public static int end_x_len = BEGIN_LEN_X + (UnitBlock.BLOCK_SIZE + BOUND_WIDTH_OF_WALL) * COLUMN_NUM + BOUND_WIDTH_OF_WALL;
    // 游戏主界面结束的y坐标
    public static int end_y_len = BEGIN_LEN_Y + (UnitBlock.BLOCK_SIZE + BOUND_WIDTH_OF_WALL) * ROW_NUM + BOUND_WIDTH_OF_WALL;
    // 调用此对象的Activity对象
    private Activity father = null;
    // 背景墙画笔
    private static Paint paintWall = null;
    // 方块单元块画笔
    private static Paint paintBlock = null;
    /**
     * 游戏状态
     */
    // 标识游戏是暂停还是运行
    private boolean runningStatus = false;
    // 标识游戏是开始还是结束
    private boolean beginGame = true;
    // 保存每行网格中包含俄罗斯方块单元的个数
    private int[] tetrisRowNum = new int[ROW_NUM];
    // 俄罗斯方块
    private Tetris tetris = null;
    // 俄罗斯方块坐标
    private List<UnitBlock> tetrisCoord = null;
    // 下一个要显示的方块
    private Tetris nextTetris = null;
    // 全部方块坐标
    private List<UnitBlock> allUnitBlock = new ArrayList<>();

    public TetrisView(Context context) {
        super(context, null);
    }

    public TetrisView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (paintWall == null) {
            paintWall = new Paint();
            //设置画笔空心
            paintWall.setStyle(Paint.Style.STROKE);
            //设置画笔颜色
            paintWall.setColor(Color.LTGRAY);
            //设置线宽
            paintWall.setStrokeWidth(BOUND_WIDTH_OF_WALL);
        }
        if (paintBlock == null) {
            paintBlock = new Paint();
            paintBlock.setColor(Color.parseColor("#FF6600"));
        }
        // 每行网格中包含俄罗斯方块单元的个数全部初始化为0
        Arrays.fill(tetrisRowNum, 0);
        //初始化俄罗斯方块
        tetris = new Tetris(BEGIN_LEN_X + COLUMN_NUM / 2 * UnitBlock.BLOCK_SIZE, BEGIN_LEN_Y, 0, 0);
        nextTetris = new Tetris(BEGIN_LEN_X + COLUMN_NUM / 2 * UnitBlock.BLOCK_SIZE, BEGIN_LEN_Y, 0, 0);
        tetrisCoord = tetris.getTetris();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 重新计算高度
        int width = end_x_len - 5 * BEGIN_LEN_X - BOUND_WIDTH_OF_WALL;
        int height = end_y_len - BEGIN_LEN_Y - BOUND_WIDTH_OF_WALL;
        // 设置宽高
        setMeasuredDimension(width, height);
    }

    /**
     * 获取NextTetrisView的俄罗斯方块
     */
    public Tetris getNextTetrisViewTetris() {
        return new Tetris(NextTetrisView.BEGIN_LEN_X,
                NextTetrisView.BEGIN_LEN_Y,
                nextTetris.getBlockType(),
                nextTetris.getColor());
    }

    /**
     * 获取下一个方块
     */
    public void getNextTetris() {
        // 获得下一个俄罗斯方块
        tetris = nextTetris;
        // 获得坐标
        tetrisCoord = tetris.getTetris();
        // 新建下一个俄罗斯方块
        nextTetris = new Tetris(BEGIN_LEN_X + COLUMN_NUM / 2 * UnitBlock.BLOCK_SIZE, BEGIN_LEN_Y, 0, 0);
        father.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        });
    }

    /**
     * 设置当前游戏页面的父类activity
     *
     * @param activity
     */
    public void setFather(Activity activity) {
        this.father = activity;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        RectF rectF;
        //因为忽略墙 会多画一次 所以减一
        for (int i = BEGIN_LEN_X; i < end_x_len - UnitBlock.BLOCK_SIZE; i += UnitBlock.BLOCK_SIZE) {
            for (int j = BEGIN_LEN_Y; j < end_y_len - UnitBlock.BLOCK_SIZE; j += UnitBlock.BLOCK_SIZE) {
                // 用矩形类设定一个矩形
                rectF = new RectF(i, j, i + UnitBlock.BLOCK_SIZE, j + UnitBlock.BLOCK_SIZE);
                // 用事先设定好的背景画笔画这个矩形，角度8，8
                canvas.drawRoundRect(rectF, 8, 8, paintWall);
            }
        }
        // 设置绘制俄罗斯方块的颜色
        paintBlock.setColor(ConstUtils.COLOR[tetris.getColor()]);
        for (int i = 0; i < tetrisCoord.size(); i++) {
            // 获取每个单位方块的坐标
            int x = tetrisCoord.get(i).getX();
            int y = tetrisCoord.get(i).getY();
            // 设置俄罗斯方块单位的坐标矩形
            rectF = new RectF(x + BOUND_WIDTH_OF_WALL, y + BOUND_WIDTH_OF_WALL,
                    x + UnitBlock.BLOCK_SIZE - BOUND_WIDTH_OF_WALL, y + UnitBlock.BLOCK_SIZE - BOUND_WIDTH_OF_WALL);
            // 用事先设定好的背景画笔画俄罗斯单位方块，角度8，8
            canvas.drawRoundRect(rectF, 8, 8, paintBlock);
        }
        // 绘制所有方块
        if (allUnitBlock.size() != 0) {
            for (int i = 0; i < allUnitBlock.size(); i++) {
                // 设置绘制俄罗斯方块的颜色
                paintBlock.setColor(ConstUtils.COLOR[allUnitBlock.get(i).getColor()]);
                // 获取每个单位方块的坐标
                int x = allUnitBlock.get(i).getX();
                int y = allUnitBlock.get(i).getY();
                // 设置俄罗斯方块单位的坐标矩形
                rectF = new RectF(x + BOUND_WIDTH_OF_WALL, y + BOUND_WIDTH_OF_WALL,
                        x + UnitBlock.BLOCK_SIZE - BOUND_WIDTH_OF_WALL, y + UnitBlock.BLOCK_SIZE - BOUND_WIDTH_OF_WALL);
                // 用事先设定好的背景画笔画俄罗斯单位方块，角度8，8
                canvas.drawRoundRect(rectF, 8, 8, paintBlock);
            }
        }
    }

    /**
     * 俄罗斯方块左移
     */
    public void toLeft() {
        if (tetrisControllerUtils.canMoveLeft(tetrisCoord, allUnitBlock)) {
            tetrisControllerUtils.toLeft(tetrisCoord);
            tetris.setX(tetris.getX() - UnitBlock.BLOCK_SIZE);
        }
        invalidate();
    }

    /**
     * 俄罗斯方块右移
     */
    public void toRight() {
        if (tetrisControllerUtils.canMoveRight(tetrisCoord, allUnitBlock)) {
            tetrisControllerUtils.toRight(tetrisCoord);
            tetris.setX(tetris.getX() + UnitBlock.BLOCK_SIZE);
        }
        invalidate();
    }

    /**
     * 俄罗斯方块下移
     */
    public boolean toDown() {
        if (tetrisControllerUtils.canMoveDown(tetrisCoord, allUnitBlock)) {
            tetrisControllerUtils.toDown(tetrisCoord);
            tetris.setY(tetris.getY() + UnitBlock.BLOCK_SIZE);
            father.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    invalidate();
                }
            });
            return true;
        }
        father.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        });
        return false;
    }

    /**
     * 俄罗斯方块旋转
     */
    public void toRotate() {
        tetrisControllerUtils.toRotate(tetris, allUnitBlock);
        invalidate();
    }

    /**
     * 判断是否满足一行
     */
    private int isRow() {
        for (int i = 0; i < tetrisRowNum.length; i++) {
            // 一行的方块数等于列数则满足一行
            if (tetrisRowNum[i] == COLUMN_NUM) {
                return i + 1;
            }
        }
        return -1;
    }

    /**
     * 消除满的行
     * @param row 指定删除行
     */
    private void removeRow(int row) {
        if (tetrisRowNum[row - 1] == COLUMN_NUM) {
            tetrisRowNum[row - 1] = 0;
            // 循环移除要消掉的行
            for (int j = allUnitBlock.size() - 1; j >= 0; j--) {
                int y = (allUnitBlock.get(j).getY() - BEGIN_LEN_Y) / UnitBlock.BLOCK_SIZE;
                if (y == row) {
                    allUnitBlock.remove(j);
                }
            }
            // 消除的一行的上方的方块整体下移
            tetrisControllerUtils.rowAboveToDown(allUnitBlock, row);
            // 通过runOnUiThread对主UI进行修改
            father.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 加分 等级乘方块数乘10
                    ((ClassicBlockActivity) father).score.setText(Integer.parseInt(((ClassicBlockActivity) father).score.getText().toString()) + COLUMN_NUM*10 + "");
                    // 设置 消除行数
                    ((ClassicBlockActivity) father).rowNum.setText(Integer.parseInt(((ClassicBlockActivity) father).rowNum.getText().toString()) + 1 + "");
                }
            });
        }
    }

    /**
     *  开始下落线程
     */
    public void startDownThread () {
        // 游戏主线程
        Thread mainThread = new Thread(new DownRunnable());
        runningStatus = true;
        mainThread.start();
    }

    /**
     * 开始或者暂停下落线程
     */
    public void startOrPauseDownThread () {
        if (runningStatus == true) {
            runningStatus = false;
        } else
            runningStatus = true;
    }

    /**
     *  关闭线程
     */
    public void colseDownThread () {
        beginGame = false;
    }

    /**
     * 判断游戏是否结束
     * @return true结束 false不结束
     */
    private static boolean isGameOver(List<UnitBlock> tetrisCoord) {
        for (int i = 0; i < tetrisCoord.size(); i++) {
            if (tetrisCoord.get(i).getY() <= BEGIN_LEN_Y ){
                return true;
            }
        }
        return false;
    }

    /**
     * 下落的线程
     */
    private class DownRunnable implements Runnable {

        @Override
        public void run() {
            while (beginGame) {
                if (runningStatus) {
                    if (!toDown()) {
                        // 如果方块超出则结束游戏
                        if (isGameOver(tetrisCoord)){
                            // 创建游戏结束的弹窗
                            father.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((ClassicBlockActivity)father).startGameOverDialog();
                                }
                            });
                            break;
                        }
                        // 重新给每行方块数赋值为0 然后重新计算每行方块数
                        Arrays.fill(tetrisRowNum, 0);
                        // 把已经固定的俄罗斯方块加入总方块
                        for (int i = 0; i < tetrisCoord.size(); i++) {
                            allUnitBlock.add(tetrisCoord.get(i));
                        }
                        // 逆向遍历所有方块
                        for (int i = allUnitBlock.size() - 1; i >= 0; i--) {
                            // y是指方块是第几行
                            int y = (allUnitBlock.get(i).getY() - BEGIN_LEN_Y) / UnitBlock.BLOCK_SIZE;
                            for (int j = tetrisRowNum.length; j > 0; j--) {
                                if (y == j)
                                    tetrisRowNum[j - 1]++;
                            }
                        }
                        //判断是否满足一行 满足则消除
                        int row;
                        while ((row = isRow()) != -1) {
                            removeRow(row);
                        }
                        //生成下一个方块
                        getNextTetris();
                        // 调用activity给下一个方块视图重新生成新方块
                        ((ClassicBlockActivity) father).setNextTetrisView();
                    }
                    try {
                        Thread.sleep(Tetris.SPEED);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}


