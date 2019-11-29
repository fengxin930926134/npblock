package com.np.block.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import com.np.block.core.model.Tetris;
import com.np.block.core.model.UnitBlock;
import com.np.block.util.ConstUtils;

import java.util.List;

public class NextTetrisView extends View {
    // 界面开始的坐标
    public static final  int BEGIN_LEN_X = 5;
    public static final  int BEGIN_LEN_Y = 5;
    private static final int BOUND_WIDTH_OF_WALL = 4;
    // 背景画笔
    private static Paint paintWall = null;
    // 方块单元块画笔
    private static Paint paintBlock = null;
    // 下一个俄罗斯方块
    private Tetris nextTetris = null;
    // 调用此对象的Activity对象
    private Activity father = null;

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
    }

    /**
     * 设置当前游戏页面的父类activity
     *
     * @param activity
     */
    public void setFather(Activity activity) {
        this.father = activity;
    }

    /**
     * 设置下一个俄罗斯方块
     * @param nextTetris
     */
    public void setNextTetris(Tetris nextTetris) {
        this.nextTetris = nextTetris;
        father.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int x = getWidth();
        int y = getHeight();
        RectF rectF;
        rectF = new RectF( BEGIN_LEN_X, BEGIN_LEN_Y, x,  y);
        canvas.drawRoundRect(rectF, 8, 8, paintWall);
        if (nextTetris != null) {
            List<UnitBlock> nextTetrisCoord = nextTetris.getTetris();
            // 设置画笔颜色
            for (int i = 0; i < nextTetrisCoord.size(); i++) {
                paintBlock.setColor(ConstUtils.COLOR[nextTetris.getColor()]);
                int mx = nextTetrisCoord.get(i).getX() + BEGIN_LEN_X + x/2;
                int my = nextTetrisCoord.get(i).getY() + BEGIN_LEN_Y + y/2 - UnitBlock.BLOCK_SIZE;
                rectF = new RectF(mx + TetrisView.BOUND_WIDTH_OF_WALL , my + TetrisView.BOUND_WIDTH_OF_WALL,
                        mx + UnitBlock.BLOCK_SIZE - TetrisView.BOUND_WIDTH_OF_WALL , my +  UnitBlock.BLOCK_SIZE  - TetrisView.BOUND_WIDTH_OF_WALL);
                canvas.drawRoundRect(rectF, 8, 8, paintBlock);
            }
        }
    }
}
