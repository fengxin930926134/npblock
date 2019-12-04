package com.np.block.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.np.block.R;
import com.np.block.base.BaseActivity;
import com.np.block.core.manager.CacheManager;
import com.np.block.core.model.Users;
import com.np.block.util.ConstUtils;

/**
 * 主界面
 * @author fengxin
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {

    /**登陆的用户基本信息*/
    private Users users;
    /**头像*/
    private ImageView headImg;

    @Override
    public void init() {
        users = (Users) CacheManager.getInstance().get(ConstUtils.USER_INFO);
        Button button = findViewById(R.id.classic_block);
        headImg = findViewById(R.id.head_img);
        button.setOnClickListener(this);
        loadHeadImg();
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

    /**
     * 加载头像图片
     */
    private void loadHeadImg() {
        RequestOptions options =new RequestOptions()

                .placeholder(R.mipmap.head)//加载成功之前占位图

                .error(R.mipmap.head)//加载错误之后的错误图

                .override(100,100)//指定图片的尺寸

                //指定图片的缩放类型为fitCenter （等比例缩放图片，宽或者是高等于ImageView的宽或者是高。）

                .fitCenter()

                //指定图片的缩放类型为centerCrop （等比例缩放图片，直到图片的狂高都大于等于ImageView的宽度，然后截取中间的显示。）

                .centerCrop()

                .circleCrop();//指定图片的缩放类型为centerCrop （圆形）
        Glide.with(this)
                .load(users.getHeadSculpture())
                .apply(options)
                .into(headImg);
    }
}
