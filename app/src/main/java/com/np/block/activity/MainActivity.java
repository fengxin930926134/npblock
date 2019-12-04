package com.np.block.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.np.block.R;
import com.np.block.base.BaseActivity;

/**
 * 主界面
 * @author fengxin
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {
    @Override
    public void init() {
        Button button = findViewById(R.id.classic_block);
        ImageView headImg = findViewById(R.id.head_img);
        RequestOptions options =new RequestOptions()

                .placeholder(R.mipmap.ic_launcher)//加载成功之前占位图

                .error(R.mipmap.ic_launcher)//加载错误之后的错误图

                .override(100,100)//指定图片的尺寸

                //指定图片的缩放类型为fitCenter （等比例缩放图片，宽或者是高等于ImageView的宽或者是高。）

                .fitCenter()

                //指定图片的缩放类型为centerCrop （等比例缩放图片，直到图片的狂高都大于等于ImageView的宽度，然后截取中间的显示。）

                .centerCrop()

                .circleCrop();//指定图片的缩放类型为centerCrop （圆形）
        Glide.with(this)
                .load("http://cdn.duitang.com/uploads/item/201501/25/20150125093346_5eJva.thumb.700_0.jpeg")
                .apply(options)
                .into(headImg);
        button.setOnClickListener(this);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.classic_block) {
            startActivity(new Intent(MainActivity.this, ClassicBlockActivity.class));
        }
    }
}
