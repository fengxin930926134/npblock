package com.np.block.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.np.block.R;
import com.np.block.adapter.GameOverAdapter;
import com.np.block.base.BaseActivity;
import com.np.block.core.enums.GameTypeEnum;
import com.np.block.core.manager.ActivityManager;
import com.np.block.core.manager.CacheManager;
import com.np.block.core.model.Users;
import com.np.block.util.ConstUtils;
import com.np.block.util.LoggerUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import butterknife.BindView;

/**
 * 游戏结束界面
 * @author fengxin
 */
public class GameOverActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.winRecyclerView)
    RecyclerView winRecyclerView;
    @BindView(R.id.defeatedRecyclerView)
    RecyclerView defeatedRecyclerView;
    @BindView(R.id.back_main_page)
    Button backMainPage;
    @BindView(R.id.another_round)
    Button anotherRound;

    @Override
    public void init() {
        //获取缓存中的数据
        List<Users> usersList = CacheManager.getInstance().getUsers(ConstUtils.CACHE_USER_BATTLE_INFO);
        //判断胜负分别加入适配器数据
        List<Users> winUsers = new ArrayList<>();
        List<Users> defeatedUsers = new ArrayList<>();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Users users = (Users) CacheManager.getInstance().get(ConstUtils.CACHE_USER_INFO);
            if (Objects.equals(extras.getString(ConstUtils.GAME_TYPE), GameTypeEnum.SINGLE_PLAYER_GAME.getCode())) {
                Users enemy = new Users();
                for (Users user: usersList) {
                    if (!user.getId().equals(users.getId())) {
                        enemy = user;
                    }
                }
                if (extras.getBoolean(ConstUtils.GAME_WIN)) {
                    winUsers.add(users);
                    defeatedUsers.add(enemy);
                } else {
                    defeatedUsers.add(users);
                    winUsers.add(enemy);
                }
            } else {
                LoggerUtils.i("双人");
            }
        }
        //分别加载胜利和失败的适配器数据
        GameOverAdapter winAdapter = new GameOverAdapter(R.layout.game_over_item, winUsers);
        GameOverAdapter defeatedAdapter = new GameOverAdapter(R.layout.game_over_item, defeatedUsers);
        winRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        winRecyclerView.setAdapter(winAdapter);
        defeatedRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        defeatedRecyclerView.setAdapter(defeatedAdapter);
        //设置点击事件
        backMainPage.setOnClickListener(this);
        anotherRound.setOnClickListener(this);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_game_over;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_main_page: {
                ActivityManager.getInstance().removeActivity(this);
                break;
            }
            case R.id.another_round: {
                break;
            }
            default:
                Toast.makeText(context, "未实现", Toast.LENGTH_SHORT).show();
        }
    }
}
