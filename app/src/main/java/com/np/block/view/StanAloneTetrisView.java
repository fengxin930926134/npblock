package com.np.block.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import com.np.block.activity.ClassicBlockActivity;
import com.np.block.activity.StandAloneBlockActivity;
import com.np.block.base.BaseTetrisView;
import com.np.block.core.model.Tetris;
import com.np.block.core.model.UnitBlock;
import java.util.List;

/**
 * 单机经典俄罗斯方块的视图
 * @author fengxin
 */
public class StanAloneTetrisView extends BaseTetrisView {

    /**此类型方块大小*/
    public static final int BLOCK_SIZE = UnitBlock.BLOCK_SIZE;

    public StanAloneTetrisView(Context context) {
        super(context);
    }

    public StanAloneTetrisView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getBlockSize() {
        return BLOCK_SIZE;
    }

    @Override
    public Tetris getNextTetris() {
        return ((ClassicBlockActivity)fatherActivity).getNextTetris();
    }

    @Override
    public void setFatherActivity(Activity activity) {
        fatherActivity = activity;
    }

    /**
     * 消除塞满方块的所有行 并且计算分数
     * @param rows 指定删除行
     */
    @Override
    public void removeRowsBlock(List<Integer> rows) {
        super.removeRowsBlock(rows);
        // 对主UI进行修改 计算得分等
        ((StandAloneBlockActivity)fatherActivity).updateDataAndUi(rows.size());
    }
}


