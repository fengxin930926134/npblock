package com.np.block.adapter;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.np.block.R;
import com.np.block.activity.MainActivity;
import com.np.block.core.model.RoomItem;
import com.np.block.util.LoggerUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 房间列表适配器
 *
 * @author fengxin
 */
public class RoomAdapter extends BaseQuickAdapter<RoomItem, BaseViewHolder> {

    private List<LinearLayout> list = new ArrayList<>();

    public RoomAdapter(int layoutResId, @Nullable List<RoomItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, RoomItem roomItem) {
        helper.setText(R.id.room_num, roomItem.getRoomId().toString());
        helper.setText(R.id.room_name, roomItem.getRoomName());
        helper.setText(R.id.room_type, roomItem.getRoomType() == 1 ? "双人排位": "单人匹配");
        helper.setText(R.id.room_user, roomItem.getCreateUser());
        LinearLayout view = helper.getView(R.id.room_item_bg);
        if (!list.contains(view)) {
            list.add(view);
        }
        view.setOnClickListener(v -> {
            ((MainActivity) mContext).roomNum = roomItem.getRoomId();
            LoggerUtils.i("当前选择了" + roomItem.getRoomId());
            for (LinearLayout linearLayout: list) {
                linearLayout.setBackground(ResourcesCompat.getDrawable(mContext.getResources(),
                        R.color.colorBlack_10, null));
            }
            helper.setBackgroundRes(R.id.room_item_bg, R.drawable.room_head_border);
        });
    }
}
