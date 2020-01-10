package com.np.block.activity;

import android.app.AlertDialog;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;
import com.np.block.R;
import com.np.block.base.BaseGameActivity;
import com.np.block.base.BaseTetrisView;
import com.np.block.core.manager.CacheManager;
import com.np.block.core.model.Stage;
import com.np.block.util.ConstUtils;
import com.np.block.util.DialogUtils;
import com.np.block.view.RushTetrisView;
import org.litepal.LitePal;
import butterknife.BindView;

/**
 * 闯关模式
 *
 * @author fengxin
 */
public class RushBlockActivity extends BaseGameActivity {
    /**俄罗斯方块视图*/
    @BindView(R.id.classic_tetris_view)
    RushTetrisView rushTetrisView;
    /**关卡名称*/
    @BindView(R.id.rush_mode_name)
    TextView rushModeName;
    /**暂停对话框*/
    private AlertDialog pauseDialog = null;
    /**关卡属性*/
    private Stage stage;
    /**消掉的行数*/
    private int rowNum = 0;

    @Override
    public BaseTetrisView getTetrisView() {
        return rushTetrisView;
    }

    @Override
    public void initData() {
        String stageType = (String) CacheManager.getInstance().get(ConstUtils.CACHE_RUSH_STAGE_TYPE);
        // 初始化关卡属性
        stage = LitePal
                .where("stageType = ?", stageType)
                .findLast(Stage.class);
        // 设置关卡名称
        rushModeName.setText(stage.getName());
        // 游戏启动
        startDownThread();
    }

    @Override
    public int getContentView() {
        return R.layout.activity_rush_block;
    }

    @Override
    protected void onPause() {
        // 暂停线程
        runningStatus = false;
        super.onPause();
    }

    @Override
    protected void onResume() {
        // 继续线程
        if (pauseDialog == null) {
            runningStatus = true;
        }
        super.onResume();
    }

    /**
     * 判断消除行数是否达成过关条件
     *
     * @param newSum 新消除行数
     */
    public void judgePassThrough(int newSum) {
        if (rowNum + newSum >= stage.getComplete()) {
            //过关
            gamePassDialog();
        } else {
            rowNum = rowNum + newSum;
        }
    }

    /**
     * 创建游戏过关的弹窗
     */
    private void gamePassDialog() {
        beginGame = false;
        // 弹出弹窗
        runOnUiThread(() -> DialogUtils.showDialog(context, "恭喜过关", "您怎么这么牛皮啊！！",
                "回到主页", "重来","开始下一关", false, false,
                //回到主页
                (dialog, which) -> {
                    dialog.cancel();
                    exitGame();
                },
                //刷新重来
                (dialog, which) -> {
                    dialog.cancel();
                    refreshGame();
                },
                //开始下一关
                (dialog, which) -> {
                    Toast.makeText(context, "尚未实现", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                    exitGame();
                },
                null));
    }

    /**
     * 创建游戏结束的弹窗
     */
    @Override
    public void startGameOverDialog() {
        super.startGameOverDialog();
        // 弹出弹窗
        runOnUiThread(() -> DialogUtils.showDialog(context, "游戏结束", "别灰心，再来一次就通关！",
                "回到主页", "重来", false, false,
                (dialog, which) -> {
                    dialog.cancel();
                    exitGame();
                },
                (dialog, which) -> {
                    dialog.cancel();
                    refreshGame();
                }));
    }

    /**
     * 创建暂停时的弹窗
     */
    private void startPauseDialog() {
        // 暂停下落
        runningStatus = false;
        pauseDialog = DialogUtils.showDialog(context, "游戏暂停", "客官，继续玩呗~！",
                "回到主页", "重来", "继续", false, true,
                //回到主页
                (dialog, which) -> {
                    dialog.cancel();
                    exitGame();
                },
                //刷新重来
                (dialog, which) -> {
                    dialog.cancel();
                    refreshGame();
                },
                //继续游戏
                (dialog, which) -> {
                    dialog.cancel();
                    runningStatus = true;
                    pauseDialog = null;
                },
                //重写返回键事件
                (dialog, keyCode, event) -> {
                    if (event.getAction() == KeyEvent.ACTION_DOWN
                            && keyCode == KeyEvent.KEYCODE_BACK
                            && event.getRepeatCount() == 0) {
                        dialog.cancel();
                        runningStatus = true;
                        pauseDialog = null;
                    }
                    return true;
                });
    }

    /**
     * 返回时触发
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0){
            if (pauseDialog == null) {
                startPauseDialog();
            }
        }
        return true;
    }
}
