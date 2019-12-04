package com.np.block.Adapter;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.np.block.R;

import java.util.List;

/**
 * 测试适配器
 * @author fengxin
 */
public class HomeAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public HomeAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, String item) {
        TextView textView = helper.getView(R.id.rank_item);
        textView.setText(item);
        final int position = helper.getLayoutPosition();
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "点了" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }
}