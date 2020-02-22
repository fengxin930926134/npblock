package com.np.block.adapter;

import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.np.block.R;
import com.np.block.core.model.TalkItem;
import java.util.List;

/**
 * 聊天内容适配器
 *
 * @author fengxin
 */
public class TalkContentAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    /**头像属性配置*/
    private RequestOptions options = new RequestOptions()
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

    public TalkContentAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(TalkItem.TYPE_LEFT, R.layout.talk_left_item);
        addItemType(TalkItem.TYPE_RIGHT, R.layout.talk_right_item);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, MultiItemEntity multiItemEntity) {
        TalkItem item = (TalkItem) multiItemEntity;
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
        //设置聊天内容
        helper.setText(R.id.item_content, item.getContent());
    }
}