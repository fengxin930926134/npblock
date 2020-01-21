package com.np.block.adapter;

import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.np.block.R;
import com.np.block.core.model.Users;
import com.np.block.util.LoggerUtils;

import java.util.List;

/**
 * 添加好友适配器
 *
 * @author fengxin
 */
public class FriendApplyAdapter extends BaseQuickAdapter<Users, BaseViewHolder> {

    /**头像属性配置*/
    private RequestOptions options =new RequestOptions()
            //加载成功之前占位图
            .placeholder(R.mipmap.np_block_launcher)
            //加载错误之后的错误图
            .error(R.mipmap.np_block_launcher)
            //指定图片的尺寸
            .override(50,50)
            //指定图片的缩放类型为fitCenter （等比例缩放图片，宽或者是高等于ImageView的宽或者是高。）
            .fitCenter()
            //指定图片的缩放类型为centerCrop （等比例缩放图片，直到图片的狂高都大于等于ImageView的宽度，然后截取中间的显示。）
            .centerCrop()
            //指定图片的缩放类型为centerCrop （圆形）
            .circleCrop();

    public FriendApplyAdapter(int layoutResId, List<Users> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Users item) {
        //设置姓名
        helper.setText(R.id.item_name, item.getGameName() != null ? item.getGameName() : "");
        //设置申请理由
        helper.setText(R.id.item_reason, item.getReason() != null ? item.getReason() : "");
        //加载头像
        ImageView rankItemImg = helper.getView(R.id.item_head);
        Glide.with(mContext)
                .load(item.getHeadSculpture())
                .apply(options)
                .into(rankItemImg);
        //设置事件
        final int position = helper.getLayoutPosition();
        rankItemImg.setOnClickListener(v -> Toast.makeText(mContext, "亚麻跌, 不要，不要点_"
                + position, Toast.LENGTH_SHORT).show());
        //设置同意申请事件
        helper.getView(R.id.agree).setOnClickListener(v ->
        {
            try {
                EMClient.getInstance().contactManager().acceptInvitation(String.valueOf(item.getId()));
                remove(position);
                notifyItemChanged(position);
            } catch (HyphenateException e) {
                LoggerUtils.e("同意好友失败:" + e.getMessage());
            }
        });
        //设置拒绝申请事件
        helper.getView(R.id.refuse).setOnClickListener(v ->
        {
            try {
                EMClient.getInstance().contactManager().declineInvitation(String.valueOf(item.getId()));
                remove(position);
                notifyItemChanged(position);
            } catch (HyphenateException e) {
                LoggerUtils.e("拒绝好友失败:" + e.getMessage());
            }
        });
    }
}