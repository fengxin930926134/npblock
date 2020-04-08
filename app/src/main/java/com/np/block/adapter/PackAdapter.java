package com.np.block.adapter;

import android.app.AlertDialog;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.np.block.R;
import com.np.block.activity.MainActivity;
import com.np.block.core.manager.ThreadPoolManager;
import com.np.block.core.model.Goods;
import com.np.block.util.ConstUtils;
import com.np.block.util.DialogUtils;
import com.np.block.util.LoggerUtils;
import com.np.block.util.OkHttpUtils;
import java.util.List;

/**
 * 商品适配器
 *
 * @author fengxin
 */
public class PackAdapter extends BaseQuickAdapter<Goods, BaseViewHolder> {

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

    public PackAdapter(int layoutResId, List<Goods> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Goods item) {
        //加载头像
        ImageView goodsImg = helper.getView(R.id.goods_img);
        Glide.with(mContext)
                .load(ConstUtils.URL + item.getGoodsPicture())
                .apply(options)
                .into(goodsImg);
        helper.setText(R.id.goods_name, item.getGoodsName() == null? "未知": item.getGoodsName());
        //设置事件
        final int position = helper.getLayoutPosition();
        goodsImg.setOnClickListener(v -> Toast.makeText(mContext, "亚麻跌, 不要，不要点_"
                + position, Toast.LENGTH_SHORT).show());
        //设置使用事件
        helper.getView(R.id.pack_use).setOnClickListener(v -> {
            AlertDialog alertDialog = DialogUtils.showDialog(mContext);
            ThreadPoolManager.getInstance().execute(() -> {
                try {
                    JSONObject params = new JSONObject();
                    params.put("recordId", item.getRecordId());
                    JSONObject response = OkHttpUtils.post("/business/useGoods", params.toJSONString());
                    if (response.getIntValue(ConstUtils.CODE) != ConstUtils.CODE_SUCCESS) {
                        throw new Exception(response.getString(ConstUtils.MSG));
                    } else {
                        ((MainActivity) mContext).runOnUiThread(() -> {
                            Toast.makeText(mContext, "使用成功", Toast.LENGTH_SHORT).show();
                            ((MainActivity) mContext).useGoods(item.getId(), position);
                        });
                    }
                } catch (Exception e) {
                    LoggerUtils.e("使用成功：" + e.getMessage());
                    ((MainActivity) mContext).runOnUiThread(() ->
                            Toast.makeText(mContext, "使用成功", Toast.LENGTH_SHORT).show());
                } finally {
                    ((MainActivity) mContext).runOnUiThread(alertDialog::cancel);
                }
            });
        });
    }
}