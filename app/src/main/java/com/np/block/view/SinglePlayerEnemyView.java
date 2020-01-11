package com.np.block.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import com.alibaba.fastjson.JSONObject;
import com.np.block.activity.SinglePlayerActivity;
import com.np.block.base.BaseTetrisView;
import com.np.block.core.model.Tetris;
import com.np.block.core.model.UnitBlock;
import com.np.block.util.ConstUtils;
import java.util.List;

/**
 * 单人匹配对战俄罗斯方块的敌人视图
 * @author fengxin
 */
public class SinglePlayerEnemyView extends BaseTetrisView {

    /**此类型方块大小*/
    public static final int BLOCK_SIZE = 40;

    public SinglePlayerEnemyView(Context context) {
        super(context);
    }

    public SinglePlayerEnemyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //清除当前方块
        tetrisUnits.clear();
        // 生成俄罗斯方块模具
        generateTetrisRectf();
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

    /**
     * 根据json数据更新敌人界面
     *
     * @param data data
     */
    public void updateView(JSONObject data) {
        //获取正确数据
        if (data != null) {
            List<UnitBlock> allBlock = JSONObject.parseArray(
                    data.getString(ConstUtils.JSON_KEY_ALL_BLOCK), UnitBlock.class);
            List<UnitBlock> tetrisBlock = JSONObject.parseArray(
                    data.getString(ConstUtils.JSON_KEY_TETRIS_BLOCK), UnitBlock.class);
            //装入容器
            allUnitBlock.clear();
            tetrisUnits.clear();
            allUnitBlock.addAll(allBlock);
            tetrisUnits.addAll(tetrisBlock);
            //生成模具
            generateTetrisRectf();
            generateAllBlockRectf();
            //刷新界面
            invalidate();
        }
    }
}


