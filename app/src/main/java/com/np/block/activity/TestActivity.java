package com.np.block.activity;

import com.np.block.R;
import com.np.block.base.BaseActivity;
import com.np.block.base.BaseTetrisView;
import com.np.block.core.enums.TetrisTypeEnum;
import com.np.block.core.model.Tetris;
import com.np.block.core.model.UnitBlock;
import com.np.block.util.LoggerUtils;
import com.np.block.util.TetrisControllerUtils;
import com.np.block.view.NextTetrisView;
import com.np.block.view.SinglePlayerEnemyView;
import com.np.block.view.SinglePlayerView;

import java.util.List;

import butterknife.BindView;

/**
 * 用做测试使用 代码不必提交
 *
 * @author fengxin
 */
public class TestActivity extends BaseActivity {

    @Override
    public void init() {
        Tetris tetris = new Tetris(BaseTetrisView.BEGIN_LEN_X + BaseTetrisView.COLUMN_NUM / 2 * SinglePlayerView.BLOCK_SIZE,
                BaseTetrisView.BEGIN_LEN_Y,
                TetrisTypeEnum.BACK_Z_SHAPE, -1, SinglePlayerView.BLOCK_SIZE);

        List<UnitBlock> tetris1 = tetris.getTetris();

        TetrisControllerUtils.toDown(tetris1, SinglePlayerEnemyView.BLOCK_SIZE);
        System.out.println();
        LoggerUtils.i(tetris1.toString());
        System.out.println();
        System.out.println();

        Tetris tetris2 = new Tetris(BaseTetrisView.BEGIN_LEN_X + BaseTetrisView.COLUMN_NUM / 2 * SinglePlayerEnemyView.BLOCK_SIZE,
                BaseTetrisView.BEGIN_LEN_Y,
                TetrisTypeEnum.BACK_Z_SHAPE, -1, SinglePlayerEnemyView.BLOCK_SIZE);


        List<UnitBlock> tetris3 = tetris2.getTetris();
        TetrisControllerUtils.toDown(tetris3, SinglePlayerEnemyView.BLOCK_SIZE);
        LoggerUtils.i(tetris3.toString());
    }


    @Override
    public int getContentView() {
        return R.layout.test_layout;
    }
}
