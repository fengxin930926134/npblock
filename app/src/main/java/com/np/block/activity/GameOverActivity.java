package com.np.block.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.np.block.R;
import com.np.block.adapter.GameOverAdapter;
import com.np.block.base.BaseActivity;
import com.np.block.core.enums.GameTypeEnum;
import com.np.block.core.enums.RankStateTypeEnum;
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
 *
 * 结算规则：
 *  初始值20方块币，胜利方初始方块币翻倍，每游戏进行一分钟加1
 *  排位初始分1000，
 *  每次胜利加20分+连胜场数，
 *  每次失败减16分+连败场数的一半，最低1000分
 *  每次满一百分进行晋级赛，晋级失败则扣20分
 * @author fengxin
 */
public class GameOverActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.winRecyclerView)
    RecyclerView winRecyclerView;
    @BindView(R.id.defeatedRecyclerView)
    RecyclerView defeatedRecyclerView;
    @BindView(R.id.winning_or_losing)
    ImageView winningOrLosing;
    /**进入晋级赛图标*/
    @BindView(R.id.rankUpgrade)
    ImageView rankUpgrade;
    /**掉段图标*/
    @BindView(R.id.rankDowngrade)
    ImageView rankDowngrade;
    /**晋级提示文本 默认失败*/
    @BindView(R.id.promotion)
    TextView promotion;
    /**回到主页*/
    @BindView(R.id.back_main_page)
    Button backMainPage;
    /**再来一局*/
    @BindView(R.id.another_round)
    Button anotherRound;
    /**排位分变化文本*/
    @BindView(R.id.rankIncreaseDecrease)
    TextView rankIncreaseDecrease;
    /**方块币增加数量文本*/
    @BindView(R.id.blockIncrease)
    TextView blockIncrease;
    /**游戏时长*/
    @BindView(R.id.gameTime)
    TextView gameTimeText;
    /**用户信息*/
    Users users;

    @Override
    public void init() {
        //获取缓存中的数据
        List<Users> usersList = CacheManager.getInstance().getUsers(ConstUtils.CACHE_USER_BATTLE_INFO);
        //判断胜负分别加入适配器数据
        List<Users> winUsers = new ArrayList<>();
        List<Users> defeatedUsers = new ArrayList<>();
        if (getIntent().getIntExtra("type", ConstUtils.HANDLER_ENTER_THE_GAME)
                == ConstUtils.HANDLER_START_CUSTOMIZATION) {
            //TODO 后期需要更改
            //设置权重为0
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0f);
            anotherRound.setVisibility(View.INVISIBLE);
            anotherRound.setLayoutParams(lp);
        }
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            users = (Users) CacheManager.getInstance().get(ConstUtils.CACHE_USER_INFO);
            int gameRank = extras.getInt(ConstUtils.GAME_RANK, 0);
            int gameBlock = extras.getInt(ConstUtils.GAME_BLOCK, 0);
            //段位状态
            String rankState = extras.getString(ConstUtils.GAME_RANK_STATE, RankStateTypeEnum.DEFAULT.getCode());
            long gameTime = extras.getLong(ConstUtils.GAME_TIME, 0);
            //rank分变化情况文本
            String symbol = " ";
            if (extras.getBoolean(ConstUtils.GAME_WIN)) {
                symbol = " +";
            } else {
                //判断是否是掉段
                boolean isDown = users.getRankScore()%100 + gameRank < 0;
                if (isDown) {
                    //显示掉段信息
                    rankDowngrade.setVisibility(View.VISIBLE);
                }
            }
            StringBuilder rankText = new StringBuilder();
            //补充排位状态信息
            switch (RankStateTypeEnum.getEnumByCode(rankState)) {
                case DEFAULT: {
                    users.setRiseInRank(false);
                    rankText.append( ConstUtils.getRankName(users.getRankScore() + gameRank, users.getRiseInRank()))
                            .append(" (").append(users.getRankScore() % 100).append(symbol)
                            .append(gameRank).append(")");
                    break;
                }
                case PROMOTION_SUCCESS_TYPE: {
                    users.setRiseInRank(false);
                    rankText.append( ConstUtils.getRankName(users.getRankScore() + gameRank, users.getRiseInRank()))
                            .append(" (0)");
                    promotion.setVisibility(View.VISIBLE);
                    //修改提示文本
                    promotion.setText(RankStateTypeEnum.PROMOTION_SUCCESS_TYPE.getDes());
                    promotion.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGreen, null));
                    break;
                }
                case GET_INTO_PROMOTION_TYPE: {
                    users.setRiseInRank(true);
                    rankText.append(ConstUtils.getRankName(users.getRankScore() + gameRank, users.getRiseInRank()))
                            .append(" (").append(users.getRankScore() % 100).append(symbol).append(gameRank).append(")");
                    rankUpgrade.setVisibility(View.VISIBLE);
                    break;
                }
                case PROMOTION_DEFEATED_TYPE: {
                    users.setRiseInRank(false);
                    rankText.append(ConstUtils.getRankName(users.getRankScore() + gameRank, users.getRiseInRank()))
                            .append(" (100").append(symbol).append(gameRank).append(")");
                    promotion.setVisibility(View.VISIBLE);
                    break;
                }
                    default:
            }
            rankIncreaseDecrease.setText(rankText.toString());
            //金币变化情况
            String blockText = "方块币 +" + gameBlock;
            blockIncrease.setText(blockText);
            String timeText = "游戏时长: " + (int)gameTime/1000/60 + "分钟";
            gameTimeText.setText(timeText);
            if (Objects.equals(extras.getString(ConstUtils.GAME_TYPE), GameTypeEnum.SINGLE_PLAYER_GAME.getCode())) {
                //单人
                Users enemy = new Users();
                for (Users user: usersList) {
                    if (!user.getId().equals(users.getId())) {
                        enemy = user;
                    }
                }
                if (extras.getBoolean(ConstUtils.GAME_WIN)) {
                    //胜利
                    winningOrLosing.setBackground(ResourcesCompat.getDrawable(getResources(), R.mipmap.win, null));
                    winUsers.add(users);
                    defeatedUsers.add(enemy);
                } else {
                    //失败
                    defeatedUsers.add(users);
                    winUsers.add(enemy);
                }
            } else {
                LoggerUtils.i("双人");
            }
            //更新当前缓存成绩
            users.setRankScore(users.getRankScore() + gameRank);
            users.setWalletBlock(users.getWalletBlock() + gameBlock);
            CacheManager.getInstance().put(ConstUtils.CACHE_USER_INFO, users);
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
                setResult(RESULT_OK, getIntent());
                ActivityManager.getInstance().removeActivity(this);
                break;
            }
            default:
                Toast.makeText(context, "未实现", Toast.LENGTH_SHORT).show();
        }
    }
}
