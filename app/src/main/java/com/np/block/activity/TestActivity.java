package com.np.block.activity;

import com.np.block.R;
import com.np.block.base.BaseGameActivity;
import com.np.block.base.BaseTetrisView;
import com.np.block.view.SinglePlayerEnemyView;
import com.np.block.view.SinglePlayerView;
import butterknife.BindView;


/**
 * 用做测试使用 代码不必提交
 *
 * @author fengxin
 */
public class TestActivity extends BaseGameActivity {

    @BindView(R.id.single_player_tetris)
    SinglePlayerView singlePlayerView;
    @BindView(R.id.single_player_tetris_enemy)
    SinglePlayerEnemyView singlePlayerEnemyView;

    @Override
    public BaseTetrisView getTetrisView() {
        return singlePlayerView;
    }

    @Override
    public void initData() {
//        startDownThread();
    }


    @Override
    public int getContentView() {
        return R.layout.test_layout;
    }
}
