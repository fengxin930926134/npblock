package com.np.block.activity;

import com.np.block.R;
import com.np.block.base.BaseActivity;
import com.np.block.core.model.Test;
import com.np.block.util.LoggerUtils;
import org.litepal.LitePal;

/**
 * 用做测试使用 代码不必提交
 *
 * @author fengxin
 */
public class TestActivity extends BaseActivity {
    @Override
    public void init() {
        // litePal测试
        Test test1 = LitePal.find(Test.class, 1);
        LoggerUtils.d("[测试] test="+ test1.toString());
    }

    @Override
    public int getContentView() {
        return R.layout.test_layout;
    }
}
