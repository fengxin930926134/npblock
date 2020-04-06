package com.np.block.adapter;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.np.block.R;
import com.np.block.core.manager.CacheManager;
import com.np.block.core.model.Users;
import com.np.block.util.ConstUtils;

import java.util.List;

/**
 * 排行榜适配器
 *
 * @author fengxin
 */
public class WealthRankAdapter extends BaseQuickAdapter<Users, BaseViewHolder> {

    /**读取当前用户信息*/
    private Users users;
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

    public WealthRankAdapter(int layoutResId, List<Users> data) {
        super(layoutResId, data);
        Object o = CacheManager.getInstance().get(ConstUtils.CACHE_USER_INFO);
        if (o instanceof Users){
            users = (Users) o;
        }
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Users item) {
        //设置成绩
        helper.setText(R.id.rank_item_name, item.getGameName() != null ? item.getGameName() : "");
        helper.setText(R.id.rank_item_score, item.getWalletBlock() != null ? item.getWalletBlock()+" 币": "0 币");
        //如果token是登陆用户
        if (users.getToken().equals(item.getToken())) {
            helper.setBackgroundRes(R.id.rank_item_background, R.color.colorTransparent);
            //防止刚更新名字时 排行榜不存在名字
            helper.setText(R.id.rank_item_name, item.getGameName() != null ? item.getGameName() : "");
        }
        //加载头像
        ImageView rankItemImg = helper.getView(R.id.rank_item_img);
        Glide.with(mContext)
                .load(item.getHeadSculpture())
                .apply(options)
                .into(rankItemImg);
        //设置事件
        final int position = helper.getLayoutPosition();
        TextView rankNum = helper.getView(R.id.rankNum);
        rankNum.setText(String.valueOf(position + 1));
        if (position == 0) {
            rankNum.setTextSize(32);
            rankNum.setTextColor(ResourcesCompat.getColor(mContext.getResources(), R.color.golden, null));
        }
        rankItemImg.setOnClickListener(v -> Toast.makeText(mContext, "亚麻跌, 不要，不要点_"
                + position, Toast.LENGTH_SHORT).show());
    }
}