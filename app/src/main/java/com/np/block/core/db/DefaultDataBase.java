package com.np.block.core.db;

import com.np.block.R;
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
        Tetris.isSave = true;
        UnitBlock.isSave = true;
        // 循环创建关卡数据
        for (StageTypeEnum stageTypeEnum: StageTypeEnum.values()) {
            if (!LitePal.isExist(Stage.class, String.format("stageType = '%s'", stageTypeEnum.getCode()))) {
                createStage(stageTypeEnum);
            }
        }
        UnitBlock.isSave = false;
        Tetris.isSave = false;
        LoggerUtils.i("db: 数据库导入数据完成...");
    }

    /**
     * 保存指定关卡数据
     *
     * @param stageType 关卡类型
     */
    private static void createStage(StageTypeEnum stageType) {
        //设置第一关
        switch (stageType) {
            case FIRST_PASS: createFirstPass(); break;
            case SECOND_PASS: createSecondPass(); break;
            case THIRD_PASS: createThreePass(); break;
            default:
                LoggerUtils.e("创建关卡类型错误：" + stageType.toString());
        }
    }

    /**
     * 创建第三关
     */
    private static void createThreePass() {
        Stage stage = new Stage();
        //设置下落速度
        stage.setSpeed(1000);
        //设置过关消除行数
        stage.setComplete(3);
        //设置关卡图标路径
        stage.setIcoPath("ico_3");
        //关卡名字
        stage.setName(StageTypeEnum.THIRD_PASS.getDes());
        //关卡类型
        stage.setStageType(StageTypeEnum.THIRD_PASS.getCode());
        //设置阻碍用的方块
        stage.getHinderTetris().add(new Tetris(
                RushTetrisView.BEGIN_LEN_X + RushTetrisView.BLOCK_SIZE * 5,
                RushTetrisView.BEGIN_LEN_Y + RushTetrisView.BLOCK_SIZE * 12,
                TetrisTypeEnum.LINE_SHAPE, -1,
                RushTetrisView.BLOCK_SIZE
        ));
        stage.getHinderTetris().add(new Tetris(
                RushTetrisView.BEGIN_LEN_X + RushTetrisView.BLOCK_SIZE,
                RushTetrisView.BEGIN_LEN_Y + RushTetrisView.BLOCK_SIZE * 18,
                TetrisTypeEnum.FIELD_SHAPE, -1,
                RushTetrisView.BLOCK_SIZE
        ));
        stage.getHinderTetris().add(new Tetris(
                RushTetrisView.BEGIN_LEN_X + RushTetrisView.BLOCK_SIZE * 9,
                RushTetrisView.BEGIN_LEN_Y + RushTetrisView.BLOCK_SIZE * 18,
                TetrisTypeEnum.FIELD_SHAPE, -1,
                RushTetrisView.BLOCK_SIZE
        ));
        stage.save();
    }

    /**
     * 创建第二关
     */
    private static void createSecondPass() {
        Stage stage = new Stage();
        //设置下落速度
        stage.setSpeed(1000);
        //设置过关消除行数
        stage.setComplete(3);
        //设置关卡图标路径
        stage.setIcoPath("ico_2");
        //关卡名字
        stage.setName(StageTypeEnum.SECOND_PASS.getDes());
        //关卡类型
        stage.setStageType(StageTypeEnum.SECOND_PASS.getCode());
        //设置阻碍用的方块
        stage.getHinderTetris().add(new Tetris(
                RushTetrisView.BEGIN_LEN_X + RushTetrisView.BLOCK_SIZE * 5,
                RushTetrisView.BEGIN_LEN_Y + RushTetrisView.BLOCK_SIZE * 12,
                TetrisTypeEnum.LINE_SHAPE, -1,
                RushTetrisView.BLOCK_SIZE
        ));
        stage.getHinderTetris().add(new Tetris(
                RushTetrisView.BEGIN_LEN_X + RushTetrisView.BLOCK_SIZE,
                RushTetrisView.BEGIN_LEN_Y + RushTetrisView.BLOCK_SIZE * 18,
                TetrisTypeEnum.FIELD_SHAPE, -1,
                RushTetrisView.BLOCK_SIZE
        ));
        stage.getHinderTetris().add(new Tetris(
                RushTetrisView.BEGIN_LEN_X + RushTetrisView.BLOCK_SIZE * 9,
                RushTetrisView.BEGIN_LEN_Y + RushTetrisView.BLOCK_SIZE * 18,
                TetrisTypeEnum.FIELD_SHAPE, -1,
                RushTetrisView.BLOCK_SIZE
        ));
        stage.save();
    }

    /**
     * 创建第一关
     */
    private static void createFirstPass() {
        Stage stage = new Stage();
        //设置下落速度
        stage.setSpeed(1000);
        //设置过关消除行数
        stage.setComplete(3);
        //设置关卡图标路径
        stage.setIcoPath("ico_1");
        //关卡名字
        stage.setName(StageTypeEnum.FIRST_PASS.getDes());
        //关卡类型
        stage.setStageType(StageTypeEnum.FIRST_PASS.getCode());
        //设置阻碍用的方块
        stage.getHinderTetris().add(new Tetris(
                RushTetrisView.BEGIN_LEN_X + RushTetrisView.BLOCK_SIZE * 2,
                RushTetrisView.BEGIN_LEN_Y + RushTetrisView.BLOCK_SIZE * 5,
                TetrisTypeEnum.LINE_SHAPE, -1,
                RushTetrisView.BLOCK_SIZE
        ));
        stage.getHinderTetris().add(new Tetris(
                RushTetrisView.BEGIN_LEN_X + RushTetrisView.BLOCK_SIZE * (RushTetrisView.COLUMN_NUM - 2),
                RushTetrisView.BEGIN_LEN_Y + RushTetrisView.BLOCK_SIZE * 16,
                TetrisTypeEnum.LINE_SHAPE, -1,
                RushTetrisView.BLOCK_SIZE
        ));
        stage.save();
    }
}
