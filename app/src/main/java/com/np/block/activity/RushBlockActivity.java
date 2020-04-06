package com.np.block.activity;

import android.app.AlertDialog;
import android.view.KeyEvent;
import android.widget.TextView;
import com.alibaba.fastjson.JSONObject;
import com.np.block.R;
import com.np.block.base.BaseGameActivity;
import com.np.block.base.BaseTetrisView;
import com.np.block.core.enums.StageTypeEnum;
import com.np.block.core.manager.CacheManager;
import com.np.block.core.manager.ThreadPoolManager;
import com.np.block.core.model.Stage;
import com.np.block.core.model.Users;
import com.np.block.util.ConstUtils;
import com.np.block.util.DialogUtils;
import com.np.block.util.LoggerUtils;
import com.np.block.util.OkHttpUtils;
import com.np.block.util.SharedPreferencesUtils;
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
    @BindView(R.id.rush_tetris_view)
    RushTetrisView rushTetrisView;
    /**关卡名称*/
    @BindView(R.id.rush_mode_name)
    TextView rushModeName;
    /**过关行数*/
    @BindView(R.id.tips_num)
    TextView tipsNum;
    /**当前消除行数*/
    @BindView(R.id.tips_current_num)
    TextView tipsCurrentNum;
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
        tipsNum.setText(String.valueOf(stage.getComplete()));
        updateRowNum();
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
            updateRowNum();
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
                    uploadRush();
                    exitGame();
                },
                //刷新重来
                (dialog, which) -> {
                    dialog.cancel();
                    refreshGame();
                },
                //开始下一关
                (dialog, which) -> {
                    dialog.cancel();
                    //上传成绩
                    uploadRush();
                    beginNextRush();
                },
                null));
    }

    /**
     * 开始下一关卡
     */
    private void beginNextRush() {
        // 缓存下一关卡
        boolean isNext = false;
        // 最大关卡
        int pass = SharedPreferencesUtils.readPass();
        // 遍历关卡num
        int i = 1;
        for (StageTypeEnum stageTypeEnum:StageTypeEnum.values()) {
            if (isNext) {
                CacheManager.getInstance().put(ConstUtils.CACHE_RUSH_STAGE_TYPE,
                        stageTypeEnum.getCode());
                //判断是否是以前未进入的关卡
                if (pass < i) {
                    //保存新关卡num
                    if (!SharedPreferencesUtils.savePass(i)) {
                        LoggerUtils.e("beginNextRush：保存关卡num失败");
                    }
                }
                break;
            }
            if (stageTypeEnum.getCode().equals(CacheManager.getInstance().get(ConstUtils.CACHE_RUSH_STAGE_TYPE))) {
                isNext = true;
            }
            i ++;
        }
        //刷新当前关卡
        refreshGame();
    }

    /**
     * 创建游戏结束的弹窗
     */
    @Override
    public void gameOver() {
        super.gameOver();
        // 弹出弹窗
        runOnUiThread(() -> DialogUtils.showDialog(context, "游戏结束", "别灰心，再来一次就通关！",
                "回到主页", "重来", false, false,
                (dialog, which) -> {
                    dialog.cancel();
                    uploadRush();
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
                    uploadRush();
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
     * 上传挑战成绩
     */
    private void uploadRush() {
        AlertDialog alertDialog = DialogUtils.showDialogDefault(context);
        Users users = (Users) CacheManager.getInstance().get(ConstUtils.CACHE_USER_INFO);
        ThreadPoolManager.getInstance().execute(() -> {
            int pass = 0;
            for (StageTypeEnum stageTypeEnum:StageTypeEnum.values()) {
                pass++;
                if (stageTypeEnum.getCode().equals(stage.getStageType())) {
                    break;
                }
            }
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("num", rowNum);
                jsonObject.put("pass", pass);
                jsonObject.put("id", users.getRushId());
                JSONObject post = OkHttpUtils.post("/rank/uploadRush", jsonObject.toString());
                if (post.getIntValue(ConstUtils.CODE) != ConstUtils.CODE_SUCCESS) {
                   throw new Exception(post.getString("msg"));
                }
                runOnUiThread(alertDialog::cancel);
            } catch (Exception e) {
                LoggerUtils.e("挑战成绩上传失败:" + e.getMessage());
                runOnUiThread(alertDialog::cancel);
            }
        });
    }

    /**
     * 更新当前消除行数
     */
    private void updateRowNum() {
        runOnUiThread(() -> tipsCurrentNum.setText(String.valueOf(rowNum)));
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
