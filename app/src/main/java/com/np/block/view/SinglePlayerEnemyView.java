package com.np.block.view;

import android.content.Context;
import android.util.AttributeSet;
import com.np.block.activity.SinglePlayerActivity;
import com.np.block.base.BaseTetrisView;
import com.np.block.core.model.Tetris;

/**
 * 单人匹配对战俄罗斯方块的敌人视图
 * @author fengxin
 */
public class SinglePlayerEnemyView extends BaseTetrisView {

    /**此类型方块大小*/
    public static final int BLOCK_SIZE = 40;
    /**调用此对象的Activity对象 父视图*/
    private SinglePlayerActivity fatherActivity = null;

    public SinglePlayerEnemyView(Context context) {
        super(context);
    }

    public SinglePlayerEnemyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setFatherActivity(Context context) {
        fatherActivity = (SinglePlayerActivity) context;
    }

    @Override
    public int getBlockSize() {
        return BLOCK_SIZE;
    }

    @Override
    public Tetris getNextTetris() {
        return fatherActivity.getNextTetris();
    }
}


