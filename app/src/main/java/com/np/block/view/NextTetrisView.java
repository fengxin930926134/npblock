package com.np.block.view;

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
import java.util.ArrayList;
import java.util.List;

/**
 * 下一个俄罗斯方块的视图
 * @author fengxin
 */
public class NextTetrisView extends View {
    /**背景墙宽*/
    private static final int BOUND_WIDTH_OF_WALL = 5;
    /**方块开始的坐标*/
    private static final  int BEGIN_LEN_X = UnitBlock.BLOCK_SIZE + BOUND_WIDTH_OF_WALL + UnitBlock.BLOCK_SIZE/2;
    private static final  int BEGIN_LEN_Y = UnitBlock.BLOCK_SIZE;
    /**背景宽高*/
    private static final int WIDTH = UnitBlock.BLOCK_SIZE * 2 + (UnitBlock.BLOCK_SIZE + BOUND_WIDTH_OF_WALL) * Tetris.TETRIS_NUMBER + BOUND_WIDTH_OF_WALL;
    private static final int HEIGHT = UnitBlock.BLOCK_SIZE * 2 + (UnitBlock.BLOCK_SIZE + BOUND_WIDTH_OF_WALL) * Tetris.TETRIS_NUMBER + BOUND_WIDTH_OF_WALL;
    /**背景画笔*/
    private static Paint paintWall = null;
    /**方块单元块画笔*/
    private static Paint paintBlock = null;
    /**下一个俄罗斯方块*/
    private Tetris nextTetris;
    /**背景矩形模具*/
    private RectF rectf;
    /**俄罗斯方块矩形模具集合*/
    private List<RectF> nextTetrisRectf = new ArrayList<>();

    public NextTetrisView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (paintWall == null){
            paintWall = new Paint();
            //设置画笔空心
            paintWall.setStyle(Paint.Style.STROKE);
            //设置画笔颜色
            paintWall.setColor(Color.LTGRAY);
            //设置线宽
            paintWall.setStrokeWidth(BOUND_WIDTH_OF_WALL);
        }
        if(paintBlock == null){
            paintBlock = new Paint();
        }
        rectf = new RectF(0, 0, WIDTH, HEIGHT);
        //初始化下一个俄罗斯方块
        nextTetris = new Tetris(BEGIN_LEN_X, BEGIN_LEN_Y, TetrisTypeEnum.DEFAULT, -1);
        // 生成模具
        generateNextTetrisRectf();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 设置宽高
        setMeasuredDimension(WIDTH, HEIGHT);
    }

    /**
     * 获取当前的（下一个俄罗斯方块）
     * @return 俄罗斯方块
     */
    public Tetris getNextTetris() {
        Tetris tetris = nextTetris;
        // 重新生成下一个俄罗斯方块
        nextTetris = new Tetris(BEGIN_LEN_X, BEGIN_LEN_Y, TetrisTypeEnum.DEFAULT, -1);
        // 重新生成模具
        generateNextTetrisRectf();
        invalidate();
        return tetris;
    }

    /**
     * 生成下一个俄罗斯方块的模具
     */
    private void generateNextTetrisRectf(){
        //俄罗斯方块的单元块集合
        List<UnitBlock> nextTetrisUnit = nextTetris.getTetris();
        if (nextTetrisRectf.size() > 0) {
            nextTetrisRectf.clear();
        }
        for (int i = 0; i < nextTetrisUnit.size(); i++) {
            int mx = nextTetrisUnit.get(i).getX() + BEGIN_LEN_X;
            int my = nextTetrisUnit.get(i).getY() + BEGIN_LEN_Y;
            nextTetrisRectf.add(new RectF(mx + TetrisView.BOUND_WIDTH_OF_WALL , my + TetrisView.BOUND_WIDTH_OF_WALL,
                    mx + UnitBlock.BLOCK_SIZE - TetrisView.BOUND_WIDTH_OF_WALL , my +  UnitBlock.BLOCK_SIZE  - TetrisView.BOUND_WIDTH_OF_WALL));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画背景
        canvas.drawRoundRect(rectf, UnitBlock.ANGLE, UnitBlock.ANGLE, paintWall);
        // 画方块
        if (nextTetrisRectf.size() > 0) {
            paintBlock.setColor(ConstUtils.COLOR[nextTetris.getColor()]);
            for (int i = 0; i < nextTetrisRectf.size(); i++) {
                canvas.drawRoundRect(nextTetrisRectf.get(i), 8, 8, paintBlock);
            }
        }
    }
}
