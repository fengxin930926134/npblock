package com.np.block.activity;

import com.np.block.R;
import com.np.block.base.BaseActivity;
import com.np.block.core.db.DefaultDataBase;
import com.np.block.core.enums.StageTypeEnum;
import com.np.block.core.model.Stage;
import com.np.block.core.model.Tetris;
import com.np.block.core.model.UnitBlock;
import com.np.block.util.LoggerUtils;
import org.litepal.LitePal;

import java.util.List;

/**
 * 用做测试使用 代码不必提交
 *
 * @author fengxin
 */
public class TestActivity extends BaseActivity {
    @Override
    public void init() {
//        String s = "[{\"classicScore\":12314,\"id\":3,\"createDate\":1575697962000},{\"classicScore\":123,\"openId\":\"12412dawd\",\"sex\":1,\"tokenTime\":1241254,\"token\":\"1241512512\",\"gameName\":\"124124\",\"phone\":\"14124\",\"name\":\"13123\",\"id\":1,\"createDate\":1575697957000}]";
//        LoggerUtils.toJson(s);
        // litePal测试
        DefaultDataBase.generateBasicDatabase();
        Stage test1 = LitePal
                .where("stageType = ?", StageTypeEnum.FIRST_PASS.getCode())
                .findLast(Stage.class);
        LoggerUtils.d("[测试] test="+ test1.toString());
        List<Tetris> allTetris = test1.getAllTetris();
        LoggerUtils.d("[测试] test="+ allTetris.toString());
        List<UnitBlock> allUnitBlock = allTetris.get(0).getAllUnitBlock();
        LoggerUtils.d("[测试] test="+ allUnitBlock.toString());
    }

    @Override
    public int getContentView() {
        return R.layout.test_layout;
    }
}
