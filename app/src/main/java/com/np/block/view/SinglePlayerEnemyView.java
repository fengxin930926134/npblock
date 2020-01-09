package com.np.block.view;

import android.content.Context;
import android.util.AttributeSet;
import com.np.block.activity.TestActivity;
import com.np.block.base.BaseTetrisView;
import com.np.block.core.model.Tetris;

/**
 * 俄罗斯方块的视图
 * @author fengxin
 */
public class SinglePlayerEnemyView extends BaseTetrisView {

    /**调用此对象的Activity对象 父视图*/
    private TestActivity fatherActivity = null;

    public SinglePlayerEnemyView(Context context) {
        super(context);
    }

    public SinglePlayerEnemyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setFatherActivity(Context context) {
        fatherActivity = (TestActivity) context;
    }

    @Override
    public int getBlockSize() {
        return 40;
    }

    @Override
    public Tetris getNextTetris() {
        return fatherActivity.getNextTetris();
    }
}


