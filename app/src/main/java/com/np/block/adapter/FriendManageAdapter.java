package com.np.block.adapter;

import android.os.Message;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.np.block.NpBlockApplication;
import com.np.block.R;
import com.np.block.core.model.Users;
import com.np.block.util.ConstUtils;
import com.np.block.util.LoggerUtils;
import java.util.List;

/**
 * 好友管理适配器
 *
 * @author fengxin
 */
public class FriendManageAdapter extends BaseQuickAdapter<Users, BaseViewHolder> {

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

    public FriendManageAdapter(int layoutResId, List<Users> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Users item) {
        //设置成绩
        helper.setText(R.id.rank_item_name, item.getGameName() != null ? item.getGameName() : "");
        helper.setText(R.id.rank_item_score, item.getClassicScore() != null ? item.getClassicScore()+"": "0");
        //加载头像
        ImageView rankItemImg = helper.getView(R.id.rank_item_img);
        Glide.with(mContext)
                .load(item.getHeadSculpture())
                .apply(options)
                .into(rankItemImg);
        //设置事件
        final int position = helper.getLayoutPosition();
        rankItemImg.setOnClickListener(v -> Toast.makeText(mContext, "亚麻跌, 不要，不要点_"
                + position, Toast.LENGTH_SHORT).show());
        //设置删除好友事件
        helper.getView(R.id.del_user).setOnClickListener(v ->
        {
            try {
                EMClient.getInstance().contactManager().deleteContact(String.valueOf(item.getId()));
                Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                remove(position);
                notifyItemChanged(position);
            } catch (HyphenateException e) {
                LoggerUtils.e("删除好友失败：" + e.getMessage());
            }
        });
        //设置联系好友事件
        helper.getView(R.id.contact_user).setOnClickListener(v ->{
            Message message = new Message();
            message.what = ConstUtils.HANDLER_CHAT_WINDOW;
            message.arg1 = item.getId();
            NpBlockApplication.getInstance().mHandler.sendMessage(message);
        });
    }
}