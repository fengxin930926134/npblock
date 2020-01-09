package com.np.block.view;

import android.content.Context;
import android.util.AttributeSet;
import com.np.block.activity.ClassicBlockActivity;
import com.np.block.base.BaseTetrisView;
import com.np.block.core.model.Tetris;
import com.np.block.core.model.UnitBlock;
import java.util.List;

/**
 * 经典俄罗斯方块的视图
 * @author fengxin
 */
public class ClassicTetrisView extends BaseTetrisView {

    /**此类型方块大小*/
    public static final int BLOCK_SIZE = UnitBlock.BLOCK_SIZE;
    /**调用此对象的Activity对象 父视图*/
    private ClassicBlockActivity fatherActivity = null;

    public ClassicTetrisView(Context context) {
        super(context);
    }

    public ClassicTetrisView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置当前游戏页面的父类activity
     * @param context 父类
     */
    public void setFatherActivity(Context context) {
        this.fatherActivity = (ClassicBlockActivity)context;
    }

    @Override
    public int getBlockSize() {
        return BLOCK_SIZE;
    }

    @Override
    public Tetris getNextTetris() {
        return fatherActivity.getNextTetris();
    }

    /**
     * 消除塞满方块的所有行 并且计算分数
     * @param rows 指定删除行
     */
    @Override
    public void removeRowsBlock(List<Integer> rows) {
        super.removeRowsBlock(rows);
        // 对主UI进行修改 计算得分等
        fatherActivity.updateDataAndUi(rows.size());
    }
}


