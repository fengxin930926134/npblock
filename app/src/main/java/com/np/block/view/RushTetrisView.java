package com.np.block.view;

import android.content.Context;
import android.util.AttributeSet;
import com.np.block.activity.RushBlockActivity;
import com.np.block.base.BaseTetrisView;
import com.np.block.core.manager.CacheManager;
import com.np.block.core.model.Stage;
import com.np.block.core.model.Tetris;
import com.np.block.core.model.UnitBlock;
import com.np.block.util.ConstUtils;
import com.np.block.util.LoggerUtils;
import org.litepal.LitePal;
import java.util.List;

/**
 * 闯关俄罗斯方块的视图
 * @author fengxin
 */
public class RushTetrisView extends BaseTetrisView {

    /**此类型方块大小*/
    public static final int BLOCK_SIZE = UnitBlock.BLOCK_SIZE;
    /**调用此对象的Activity对象 父视图*/
    private RushBlockActivity fatherActivity = null;

    public RushTetrisView(Context context) {
        super(context, null);
    }

    public RushTetrisView(Context context, AttributeSet attrs) {
        super(context, attrs);
        String stageType = (String) CacheManager.getInstance().get(ConstUtils.CACHE_RUSH_STAGE_TYPE);
        // 初始化阻碍方块
        Stage hinderTetris = LitePal
                .where("stageType = ?", stageType)
                .findLast(Stage.class);
        List<Tetris> allTetris = hinderTetris.getAllTetris();
        for (int i = 0; i < allTetris.size(); i++) {
            LoggerUtils.i(allTetris.toString());
            allUnitBlock.addAll(allTetris.get(i).getAllUnitBlock());
        }
        // 生成所有方块模具
        generateAllBlockRectf();
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
     * 设置当前游戏页面的父类activity
     * @param context 父类
     */
    public void setFatherActivity(Context context) {
        this.fatherActivity = (RushBlockActivity)context;
    }

    /**
     * 消除塞满方块的所有行
     * @param rows 指定删除行
     */
    @Override
    public void removeRowsBlock(List<Integer> rows) {
        super.removeRowsBlock(rows);
        // 判断是否过关
        fatherActivity.judgePassThrough(rows.size());
    }
}


