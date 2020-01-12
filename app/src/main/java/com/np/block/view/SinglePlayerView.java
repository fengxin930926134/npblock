package com.np.block.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import com.np.block.activity.SinglePlayerActivity;
import com.np.block.base.BaseTetrisView;
import com.np.block.core.enums.TetrisTypeEnum;
import com.np.block.core.model.Tetris;
import com.np.block.core.model.UnitBlock;
import com.np.block.util.TetrisControllerUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * 单人匹配对战俄罗斯方块的视图
 * @author fengxin
 */
public class SinglePlayerView extends BaseTetrisView {

    /**此类型方块大小*/
    public static final int BLOCK_SIZE = UnitBlock.BLOCK_SIZE;
    /**用于传送的小型俄罗斯方块*/
    private Tetris tetrisSmall;
    /**用于传送的所有小型俄罗斯方块*/
    private List<UnitBlock> allBlockSmall = new ArrayList<>();
    private int xEndSmall;
    private int yEndSmall;

    public SinglePlayerView(Context context) {
        super(context);
    }

    public SinglePlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        xEndSmall = BEGIN_LEN_X + (SinglePlayerEnemyView.BLOCK_SIZE + BOUND_WIDTH_OF_WALL) * COLUMN_NUM + BOUND_WIDTH_OF_WALL;
        yEndSmall = BEGIN_LEN_Y + (SinglePlayerEnemyView.BLOCK_SIZE + BOUND_WIDTH_OF_WALL) * ROW_NUM + BOUND_WIDTH_OF_WALL;
        //初始化小型俄罗斯方块
        tetrisSmall = new Tetris(
                BEGIN_LEN_X + COLUMN_NUM / 2 * SinglePlayerEnemyView.BLOCK_SIZE,
                BEGIN_LEN_Y, TetrisTypeEnum.getEnumByCode(tetris.getTetrisType()),
                tetris.getColor(),
                SinglePlayerEnemyView.BLOCK_SIZE);
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
        for (int row : rows) {
            // 循环移除要消掉的行
            TetrisControllerUtils.removeLine(allBlockSmall, row);
            // 消除的一行的上方的方块整体下移
            TetrisControllerUtils.blockRowsToDown(allBlockSmall, row, SinglePlayerEnemyView.BLOCK_SIZE);
        }
        super.removeRowsBlock(rows);
        //清除俄罗斯方块后的操作
        ((SinglePlayerActivity)fatherActivity).updateDataAndUi(rows.size());
    }

    @Override
    public void toLeftAfter() {
        super.toLeftAfter();
        TetrisControllerUtils.toLeft(tetrisSmall.getTetris(), SinglePlayerEnemyView.BLOCK_SIZE);
    }

    @Override
    public void toRightAfter() {
        super.toRightAfter();
        TetrisControllerUtils.toRight(tetrisSmall.getTetris(), SinglePlayerEnemyView.BLOCK_SIZE);
    }

    @Override
    public void toDownAfter() {
        super.toDownAfter();
        TetrisControllerUtils.toDown(tetrisSmall.getTetris(), SinglePlayerEnemyView.BLOCK_SIZE);
    }

    @Override
    public void toRotate() {
        super.toRotate();
        TetrisControllerUtils.toRotate(tetrisSmall, allBlockSmall, xEndSmall, yEndSmall, SinglePlayerEnemyView.BLOCK_SIZE);
    }

    @Override
    public void generateTetris() {
        super.generateTetris();
        //生成小型俄罗斯方块
        tetrisSmall = new Tetris(
                BEGIN_LEN_X + COLUMN_NUM / 2 * SinglePlayerEnemyView.BLOCK_SIZE,
                BEGIN_LEN_Y, TetrisTypeEnum.getEnumByCode(tetris.getTetrisType()),
                tetris.getColor(),
                SinglePlayerEnemyView.BLOCK_SIZE);
    }

    @Override
    public void addBlockToAll() {
        super.addBlockToAll();
        allBlockSmall.addAll(tetrisSmall.getTetris());
    }

    public Tetris getTetrisSmall() {
        return tetrisSmall;
    }

    public List<UnitBlock> getAllBlockSmall() {
        return allBlockSmall;
    }
}
