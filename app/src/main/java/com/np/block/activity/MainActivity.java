package com.np.block.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.np.block.adapter.ClassicRankAdapter;
import com.np.block.R;
import com.np.block.base.BaseActivity;
import com.np.block.core.manager.CacheManager;
import com.np.block.core.manager.ThreadPoolManager;
import com.np.block.core.model.Users;
import com.np.block.util.ConstUtils;
import com.np.block.util.LogUtils;
import com.np.block.util.OkHttpUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 主界面
 * @author fengxin
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {

    /**登陆的用户基本信息*/
    private static Users users = null;
    /**头像*/
    private ImageView headImg;
    /**排行榜适配器*/
    private ClassicRankAdapter classicRankAdapter;
    /**适配器数据*/
    private static List<Users> adapterDate = new ArrayList<>();

    @Override
    public void init() {
        users = (Users) CacheManager.getInstance().get(ConstUtils.CACHE_USER_INFO);
        Button button = findViewById(R.id.classic_block);
        headImg = findViewById(R.id.head_img);
        TextView userName = findViewById(R.id.user_name);
        // 初始化用户名
        userName.setText(users.getName() != null ? users.getName(): getResources().getString(R.string.app_name));
        RecyclerView recyclerView = findViewById(R.id.ranking);
        button.setOnClickListener(this);
        // 加载头像
        loadHeadImg();
        // 初始化适配器数据
        refreshRankData();
        // 构建适配器
        classicRankAdapter = new ClassicRankAdapter(R.layout.rank_item, adapterDate);
        // 设置布局管理器
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 设置适配器
        recyclerView.setAdapter(classicRankAdapter);
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

    @Override
    protected void onResume() {
        // 判断是否存在未上传成绩
        if (CacheManager.getInstance().containsKey(ConstUtils.CACHE_WAIT_UPLOAD_CLASSIC_SCORE)) {
            final int score = (int) CacheManager.getInstance().get(ConstUtils.CACHE_WAIT_UPLOAD_CLASSIC_SCORE);
            ThreadPoolManager.getInstance().execute(() -> {
                users.setClassicScore(score);
                try {
                    String response = OkHttpUtils.post("/rank/uploadClassic", JSONObject.toJSONString(users));
                    JSONObject data = JSONObject.parseObject(response);
                    //解析返回数据
                    if (data.getIntValue(ConstUtils.STATUS) == ConstUtils.STATUS_SUCCESS) {
                        data = data.getJSONObject("body");
                        if (data.getIntValue(ConstUtils.CODE) == ConstUtils.CODE_SUCCESS){
                            refreshRankData();
                            // 清缓存
                            CacheManager.getInstance().remove(ConstUtils.CACHE_WAIT_UPLOAD_CLASSIC_SCORE);
                        }
                    }
                } catch (IOException e) {
                    LogUtils.e(TAG, e.getMessage());
                }
            });
        }
        super.onResume();
    }

    /**
     * 刷新排行榜adapter的数据
     */
    private void refreshRankData(){
        ThreadPoolManager.getInstance().execute(() -> {
            // 获取最新排行榜数据
            try {
                String response = OkHttpUtils.post("/rank/classic", JSONObject.toJSONString(users));
                JSONObject data = JSONObject.parseObject(response);
                if (data.getIntValue(ConstUtils.STATUS) == ConstUtils.STATUS_SUCCESS) {
                    data = data.getJSONObject("body");
                    if (data.getIntValue(ConstUtils.CODE) == ConstUtils.CODE_SUCCESS){
                        adapterDate.clear();
                        adapterDate.addAll(JSONObject.parseArray(data.getString("result"), Users.class));
                        runOnUiThread(() -> {
                            // 刷新数据
                            classicRankAdapter.notifyDataSetChanged();
                        });
                    }else {
                        //获取失败
                        LogUtils.i(TAG, data.getString("msg"));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 加载头像图片
     */
    private void loadHeadImg() {
        RequestOptions options =new RequestOptions()
                //加载成功之前占位图
                .placeholder(R.mipmap.head)
                //加载错误之后的错误图
                .error(R.mipmap.head)
                //指定图片的尺寸
                .override(100,100)
                //指定图片的缩放类型为fitCenter （等比例缩放图片，宽或者是高等于ImageView的宽或者是高。）
                .fitCenter()
                //指定图片的缩放类型为centerCrop （等比例缩放图片，直到图片的狂高都大于等于ImageView的宽度，然后截取中间的显示。）
                .centerCrop();
        // android10无法加载 大概是由于http请求被拦截
        Glide.with(this)
                .load(users.getHeadSculpture())
                .apply(options)
                .into(headImg);
    }
}
