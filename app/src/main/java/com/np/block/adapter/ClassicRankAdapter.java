package com.np.block.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.np.block.R;
import com.np.block.core.model.Users;
import java.util.List;

/**
 * 测试适配器
 * @author fengxin
 */
public class ClassicRankAdapter extends BaseQuickAdapter<Users, BaseViewHolder> {

    /**头像属性配置*/
    private RequestOptions options =new RequestOptions()
            //加载成功之前占位图
            .placeholder(R.mipmap.head)
            //加载错误之后的错误图
            .error(R.mipmap.head)
            //指定图片的尺寸
            .override(50,50)
            //指定图片的缩放类型为fitCenter （等比例缩放图片，宽或者是高等于ImageView的宽或者是高。）
            .fitCenter()
            //指定图片的缩放类型为centerCrop （等比例缩放图片，直到图片的狂高都大于等于ImageView的宽度，然后截取中间的显示。）
            .centerCrop()
            //指定图片的缩放类型为centerCrop （圆形）
            .circleCrop();

    public ClassicRankAdapter(int layoutResId, @Nullable List<Users> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Users item) {
        ImageView rankItemImg = helper.getView(R.id.rank_item_img);
        TextView rankItemName = helper.getView(R.id.rank_item_name);
        TextView rankItemScore = helper.getView(R.id.rank_item_score);
        //设置成绩
        rankItemName.setText(item.getName() != null ? item.getName() : "");
        rankItemScore.setText(item.getClassicScore() != null ? item.getClassicScore()+"": "0");
        //加载头像
        Glide.with(mContext)
                .load(item.getHeadSculpture())
                .apply(options)
                .into(rankItemImg);
        //设置事件
        final int position = helper.getLayoutPosition();
        rankItemImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "不要点第" + position + "头像了啊！！还没实现！！", Toast.LENGTH_SHORT).show();
            }
        });
    }
}