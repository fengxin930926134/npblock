package com.np.block.core.db;

import com.np.block.core.enums.StageTypeEnum;
import com.np.block.core.enums.TetrisTypeEnum;
import com.np.block.core.model.Stage;
import com.np.block.core.model.Tetris;
import com.np.block.core.model.UnitBlock;
import com.np.block.util.LoggerUtils;
import com.np.block.view.RushTetrisView;
import org.litepal.LitePal;

/**
 * 管理数据库基础数据
 * @author fengxin
 */
public class DefaultDataBase {

    private DefaultDataBase(){}

    /**
     * 生成基础数据库
     */
    public static void generateBasicDatabase() {
        LitePal.getDatabase();
        // 循环创建关卡数据
        for (StageTypeEnum stageTypeEnum: StageTypeEnum.values()) {
            if (!LitePal.isExist(Stage.class, String.format("stageType = '%s'", stageTypeEnum.getCode()))) {
                createStage(stageTypeEnum);
            }
        }
    }

    /**
     * 保存指定关卡数据
     *
     * @param stageType 关卡类型
     */
    private static void createStage(StageTypeEnum stageType) {
        Tetris.isSave = true;
        UnitBlock.isSave = true;
        Stage stage = new Stage();
        //设置第一关
        if (stageType == StageTypeEnum.FIRST_PASS) {
            //设置下落速度
            stage.setSpeed(1000);
            //设置过关消除行数
            stage.setComplete(3);
            //关卡名字
            stage.setName(stageType.getDes());
            //关卡类型
            stage.setStageType(stageType.getCode());
            //设置阻碍用的方块
            stage.getHinderTetris().add(new Tetris(
                    RushTetrisView.BEGIN_LEN_X + UnitBlock.BLOCK_SIZE * 2,
                    RushTetrisView.BEGIN_LEN_Y + UnitBlock.BLOCK_SIZE * 5,
                    TetrisTypeEnum.LINE_SHAPE, -1,
                    UnitBlock.BLOCK_SIZE
            ));
            stage.getHinderTetris().add(new Tetris(
                    RushTetrisView.BEGIN_LEN_X + UnitBlock.BLOCK_SIZE * (RushTetrisView.COLUMN_NUM - 2),
                    RushTetrisView.BEGIN_LEN_Y + UnitBlock.BLOCK_SIZE * 16,
                    TetrisTypeEnum.LINE_SHAPE, -1,
                    UnitBlock.BLOCK_SIZE
            ));
            stage.save();
        }
        UnitBlock.isSave = false;
        Tetris.isSave = false;
        LoggerUtils.i("db: 数据库导入数据完成...");
    }
}
