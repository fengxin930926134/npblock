package com.np.block.activity;

import com.np.block.R;
import com.np.block.base.BaseActivity;
import com.np.block.core.model.Tetris;
import com.np.block.view.NextTetrisView;
import com.np.block.view.SinglePlayerEnemyView;

import butterknife.BindView;

/**
 * 用做测试使用 代码不必提交
 *
 * @author fengxin
 */
public class TestActivity extends BaseActivity {

    @BindView(R.id.single_player_tetris)
    SinglePlayerEnemyView singlePlayerEnemyView;
    /**下一个俄罗斯方块视图*/
    @BindView(R.id.next_tetris_view)
    NextTetrisView nextTetris;
    @Override
    public void init() {
        singlePlayerEnemyView.setFatherActivity(this);
    }

    /**
     * 从下一个方块视图里获取方块
     */
    public Tetris getNextTetris() {
        return nextTetris.getNextTetris();
    }

    @Override
    public int getContentView() {
        return R.layout.test_layout;
    }
}
