package com.np.block.activity;

import android.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.np.block.R;
import com.np.block.base.BaseActivity;
import com.np.block.core.enums.StageTypeEnum;
import com.np.block.core.manager.ActivityManager;
import com.np.block.core.manager.CacheManager;
import com.np.block.core.manager.ThreadPoolManager;
import com.np.block.core.model.Stage;
import com.np.block.core.model.Tetris;
import com.np.block.util.ConstUtils;
import com.np.block.util.DialogUtils;
import com.np.block.view.NextTetrisView;
import com.np.block.view.RushTetrisView;
import org.litepal.LitePal;
import butterknife.BindView;

/**
 * 闯关模式
 *
 * @author fengxin
 */
public class RushBlockActivity extends BaseActivity implements View.OnClickListener {
    /**俄罗斯方块视图*/
    @BindView(R.id.rush_tetris_view)
    RushTetrisView tetris;
    /**下一个俄罗斯方块视图*/
    @BindView(R.id.nextTetrisView)
    NextTetrisView nextTetris;
    /**左移按钮*/
    @BindView(R.id.left)
    Button left;
    /**右移按钮*/
    @BindView(R.id.right)
    Button right;
    /**下落按钮*/
    @BindView(R.id.down)
    Button down;
    /**旋转按钮*/
    @BindView(R.id.rotate)
    Button rotate;
    @BindView(R.id.rush_mode_name)
    TextView rushModeName;
    /**暂停对话框*/
    private AlertDialog pauseDialog = null;
    /**判断是否长点击*/
    private boolean isLongClick = false;
    /**标识游戏是暂停还是运行*/
    private boolean runningStatus = true;
    /**标识游戏是开始还是结束*/
    private boolean beginGame = true;
    /**关卡属性*/
    private Stage stage;
    /**消掉的行数*/
    private int rowNum = 0;

    @Override
    public void init() {
        String stageType = (String) CacheManager.getInstance().get(ConstUtils.CACHE_RUSH_STAGE_TYPE);
        // 初始化关卡属性
        stage = LitePal
                .where("stageType = ?", stageType)
                .findLast(Stage.class);
        // 设置关卡名称
        rushModeName.setText(stage.getName());
        // 设置按钮单击事件
        left.setOnClickListener(this);
        right.setOnClickListener(this);
        down.setOnClickListener(this);
        rotate.setOnClickListener(this);
        down.setOnLongClickListener(v -> {
            //启动长按下落线程
            startDownLongThread();
            // 如果让回调消耗该长按，返回true，否则false，如果false，其他地方监听生效
            return false;
        });
        // 设置子视图对象的父视图
        tetris.setFatherActivity(this);
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
     * 游戏界面的单击事件
     *
     * @param view 被单击对象
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left : tetris.toLeft(); break;
            case R.id.right : tetris.toRight(); break;
            case R.id.down : downEvent();break;
            case R.id.rotate : tetris.toRotate(); break;
            default:
                Toast.makeText(context, "尚未实现", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 从下一个方块视图里获取方块
     */
    public Tetris getNextTetris() {
        return nextTetris.getNextTetris();
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
                    dialog.cancel();
                    Toast.makeText(context, "尚未实现", Toast.LENGTH_SHORT).show();
                },
                null));
    }

    /**
     * 创建游戏结束的弹窗
     */
    private void startGameOverDialog() {
        beginGame = false;
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

    /**
     * 下落按钮的点击事件
     */
    private void downEvent() {
        if (!isLongClick && beginGame) {
            if (tetris.toDown()) {
                beginGame = false;
            }
        }else {
            isLongClick = false;
        }
    }

    /**
     * 退出游戏
     */
    private void exitGame() {
        //关闭游戏
        beginGame = false;
        finish();
        ActivityManager.getInstance().removeActivity(this);
    }

    /**
     * 刷新游戏
     * 调用recreate方法重新创建Activity会比正常启动Activity多调用了onSaveInstanceState()和
     * onRestoreInstanceState()两个方法，onSaveInstanceState()会在onCreate方法之前调用。
     * 所以可以在onCreate()方法中获取onSaveInstanceState()保存的Theme数据
     */
    private void refreshGame() {
        recreate();
    }

    /**
     * 启动下落游戏线程
     */
    private void startDownThread () {
        ThreadPoolManager.getInstance().execute(() -> {
            while (beginGame) {
                if (runningStatus) {
                    //下移
                    down();
                    try {
                        Thread.sleep(stage.getSpeed());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 启动长按下落线程
     */
    private void startDownLongThread() {
        isLongClick = true;
        ThreadPoolManager.getInstance().execute(() -> {
            do {
                down();
                try {
                    Thread.sleep(80);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (isLongClick && beginGame);
        });
    }

    /**
     * 在ui线程中执行方块下落
     */
    private synchronized void down() {
        //下移
        runOnUiThread(() -> {
            if (tetris.toDown()) {
                // 启动游戏结束的弹窗
                startGameOverDialog();
            }
        });
    }
}
