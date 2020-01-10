package com.np.block.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import com.np.block.activity.SinglePlayerActivity;
import com.np.block.base.BaseTetrisView;
import com.np.block.core.model.Tetris;
import com.np.block.core.model.UnitBlock;
import java.util.List;

/**
 * 单人匹配对战俄罗斯方块的视图
 * @author fengxin
 */
public class SinglePlayerView extends BaseTetrisView {

    /**此类型方块大小*/
    public static final int BLOCK_SIZE = UnitBlock.BLOCK_SIZE;

    public SinglePlayerView(Context context) {
        super(context);
    }

    public SinglePlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getBlockSize() {
        return BLOCK_SIZE;
    }

    @Override
    public Tetris getNextTetris() {
        return ((SinglePlayerActivity)fatherActivity).getNextTetris();
    }

    @Override
    public void setFatherActivity(Activity activity) {
        fatherActivity = activity;
    }

    @Override
    public void removeRowsBlock(List<Integer> rows) {
        super.removeRowsBlock(rows);
        //清除俄罗斯方块后的操作
        ((SinglePlayerActivity)fatherActivity).updateDataAndUi(rows.size());
    }
}
