package com.np.block.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.viewpager.widget.ViewPager;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.NetUtils;
import com.np.block.R;
import com.np.block.adapter.ClassicRankAdapter;
import com.np.block.base.BaseActivity;
import com.np.block.core.manager.CacheManager;
import com.np.block.core.manager.ThreadPoolManager;
import com.np.block.core.model.Users;
import com.np.block.fragment.ClassicRankFragment;
import com.np.block.fragment.RankingRankFragment;
import com.np.block.fragment.RushRankFragment;
import com.np.block.util.ConstUtils;
import com.np.block.util.DialogUtils;
import com.np.block.util.LoggerUtils;
import com.np.block.util.OkHttpUtils;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import java.util.List;
import butterknife.BindView;

/**
 * 主界面
 * @author fengxin
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {

    /**登陆的用户基本信息*/
    private static Users users = null;
    /**头像*/
    @BindView(R.id.head_img)
    ImageView headImg;
    /**主页的用户游戏名字*/
    @BindView(R.id.user_name)
    TextView userName;
    /**经典模式按钮*/
    @BindView(R.id.classic_block)
    Button classic;
    /**对战模式按钮*/
    @BindView(R.id.battle_block)
    Button battle;
    /**趣味模式按钮*/
    @BindView(R.id.interest_block)
    Button interest;
    /**经典模式排行榜适配器*/
    public ClassicRankAdapter classicRankAdapter = null;

    @Override
    public void init() {
        users = (Users) CacheManager.getInstance().get(ConstUtils.CACHE_USER_INFO);
        // 初始化用户名
        userName.setText(users.getGameName() != null ? users.getGameName(): getResources().getString(R.string.app_name));
        // 初始化点击事件
        classic.setOnClickListener(this);
        battle.setOnClickListener(this);
        interest.setOnClickListener(this);
        // 加载头像
        loadHeadImg();

        //-----排行榜相关
        //创建标签组
        ViewGroup tab = findViewById(R.id.tab);
        //添加标签组的子视图
        tab.addView(LayoutInflater.from(this).inflate(R.layout.rank_title_effect, tab, false));

        //设置页
        ViewPager viewPager = findViewById(R.id.viewpager);
        SmartTabLayout viewPagerTab = findViewById(R.id.viewPagerTab);

        //循环生成每一个Fragment
        FragmentPagerItems pages = new FragmentPagerItems(this);
        pages.add(FragmentPagerItem.of(getString(R.string.classic_block_text), ClassicRankFragment.class));
        pages.add(FragmentPagerItem.of(getString(R.string.battle_block_text), RankingRankFragment.class));
        pages.add(FragmentPagerItem.of(getString(R.string.interest_block_text), RushRankFragment.class));

        //创建页面切换组件的适配器（pages类似于list，类似页面的集合）
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), pages);

        //设置页面切换组件的适配器
        viewPager.setAdapter(adapter);
        //设置tab组件关联的viewPage
        viewPagerTab.setViewPager(viewPager);

        //-----排行榜相关

        //注册一个监听连接状态的listener
        EMClient.getInstance().addConnectionListener(new EmClientConnectionListener());
    }

    @Override
    public int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.classic_block:
                startActivity(new Intent(MainActivity.this, ClassicBlockActivity.class));
                break;
            case R.id.battle_block:
                battleDialog();
                break;
            case R.id.interest_block:
                interestDialog();
                break;
            default: Toast.makeText(context, "尚未实现", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 趣味模式弹窗
     */
    private void interestDialog() {
        AlertDialog dialog = DialogUtils.showDialogDefault(context);
        View view = View.inflate(context, R.layout.alert_dialog_interest, null);
        view.findViewById(R.id.alert_interest_finish).setOnClickListener(v -> dialog.cancel());
        dialog.setContentView(view);
    }

    /**
     * 对战模式弹窗
     */
    private void battleDialog() {
        final AlertDialog dialog = DialogUtils.showDialogDefault(context);
        View view = View.inflate(context, R.layout.alert_dialog_battle, null);
        view.findViewById(R.id.alert_battle_finish).setOnClickListener(v -> dialog.cancel());
        dialog.setContentView(view);
    }

    @Override
    protected void onResume() {
        // 判断是否存在未上传成绩
        if (CacheManager.getInstance().containsKey(ConstUtils.CACHE_WAIT_UPLOAD_CLASSIC_SCORE)) {
            final int score = (int) CacheManager.getInstance().get(ConstUtils.CACHE_WAIT_UPLOAD_CLASSIC_SCORE);
            ThreadPoolManager.getInstance().execute(() -> {
                users.setClassicScore(score);
                try {
                    JSONObject response = OkHttpUtils.post("/rank/uploadClassic", JSONObject.toJSONString(users));
                    //解析返回数据
                    if (response.getIntValue(ConstUtils.CODE) == ConstUtils.CODE_SUCCESS){
                        refreshRankData();
                        // 清缓存
                        CacheManager.getInstance().remove(ConstUtils.CACHE_WAIT_UPLOAD_CLASSIC_SCORE);
                    }
                } catch (Exception e) {
                    LoggerUtils.e(e.getMessage());
                }
            });
        }
        super.onResume();
    }

    /**
     * 刷新排行榜数据 TODO 存在bug
     */
    private void refreshRankData(){
        ThreadPoolManager.getInstance().execute(() -> {
            // 获取最新排行榜数据
            try {
                JSONObject response = OkHttpUtils.post("/rank/classic", JSONObject.toJSONString(users));
                if (response.getIntValue(ConstUtils.CODE) == ConstUtils.CODE_SUCCESS){
                    List<Users> usersList = JSONObject.parseArray(response.getString("result"), Users.class);
                    CacheManager.getInstance().put(ConstUtils.CACHE_RANK_CLASSICAL_MODE, usersList);
                    runOnUiThread(() -> {
                        // 刷新数据
                        if (classicRankAdapter != null) {
                            classicRankAdapter.notifyDataSetChanged();
                            LoggerUtils.i("执行刷新了");
                        }
                    });
                }else {
                    //获取失败
                    LoggerUtils.toJson(response.toJSONString());
                }
            } catch (Exception e) {
                LoggerUtils.e(e.getMessage());
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
        Glide.with(this)
                .load(users.getHeadSculpture())
                .apply(options)
                .into(headImg);

        headImg.setOnClickListener(v -> {
            //测试
            LoggerUtils.i(String.valueOf(classicRankAdapter == null));
        });
    }

    /**
     * 实现ConnectionListener接口
     */
    private class EmClientConnectionListener implements EMConnectionListener {
        @Override
        public void onConnected() {
        }
        @Override
        public void onDisconnected(final int error) {
            runOnUiThread( ()-> {
                if(error == EMError.USER_REMOVED){
                    // 显示帐号已经被移除
                    Toast.makeText(context, "垃圾东西，滚", Toast.LENGTH_SHORT).show();
                }else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                    // 显示帐号在其他设备登录 做弹窗强制下线
                    Toast.makeText(context, "其他地方登陆，马上给我滚", Toast.LENGTH_SHORT).show();
                } else {
                    if (NetUtils.hasNetwork(MainActivity.this)){
                        //连接不到聊天服务器
                        LoggerUtils.i("正在连接聊天服务器");
                    }else {
                        //当前网络不可用，请检查网络设置
                        Toast.makeText(context, "当前网络不可用，请检查网络设置", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
