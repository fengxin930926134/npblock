package com.np.block.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * 系统屏幕的操作工具
 * @author fengxin
 */
public class ResolutionUtils {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2Px(Context context, float dpValue) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpValue, r.getDisplayMetrics());
        return (int) px;
    }
}