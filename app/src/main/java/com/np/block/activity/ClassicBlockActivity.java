package com.np.block.activity;

import android.app.AlertDialog;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;
import com.np.block.R;
import com.np.block.base.BaseGameActivity;
import com.np.block.base.BaseTetrisView;
import com.np.block.core.manager.CacheManager;
import com.np.block.core.model.Tetris;
import com.np.block.core.model.Users;
import com.np.block.util.ConstUtils;
import com.np.block.util.DialogUtils;
import com.np.block.util.LoggerUtils;
import com.np.block.util.SharedPreferencesUtils;
import com.np.block.view.ClassicTetrisView;
import butterknife.BindView;

/**
 * 经典模式
 * @author fengxin
 */
public class ClassicBlockActivity extends BaseGameActivity {
    /**俄罗斯方块视图*/
    @BindView(R.id.classic_tetris_view)
    ClassicTetrisView classicTetrisView;
    /**分数*/
    @BindView(R.id.score)
    TextView score;
    /**等级*/
    @BindView(R.id.grade)
    TextView grade;
    /**消掉的行数*/
    @BindView(R.id.row_num)
    TextView rowNum;
    /**最高分文本*/
    @BindView(R.id.max_score)
    TextView maxScoreText;
    /**最高分*/
    private int maxScore;
    /**暂停对话框*/
    private AlertDialog pauseDialog = null;

    @Override
    public BaseTetrisView getTetrisView() {
        return classicTetrisView;
    }

    @Override
    public void initData() {
        // 初始化最高分 当本地数据没有的时候读取缓存数据
        if ((maxScore = SharedPreferencesUtils.readScore()) == 0) {
            Users user = (Users) CacheManager.getInstance().get(ConstUtils.CACHE_USER_INFO);
            if (user != null && user.getClassicScore() != null && user.getClassicScore() > 0) {
                maxScore = user.getClassicScore();
                // 保存本地
                if (SharedPreferencesUtils.saveScore(maxScore)) {
                    LoggerUtils.i("[SP] 保存成绩失败");
                }
            }
        }
        maxScoreText.setText(String.valueOf(maxScore));
        // 启动下落线程  咳咳  游戏启动
        startDownThread();
    }

    @Override
    public int getContentView() {
        return R.layout.activity_classic_block;
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
     * 从下一个方块视图里获取方块
     */
    @Override
    public Tetris getNextTetris() {
        //下落一个块+1分
        int newScore = Integer.parseInt(score.getText().toString()) + 1;
        runOnUiThread(() -> score.setText(String.valueOf(newScore)));
        return super.getNextTetris();
    }

    /**
     * 更新游戏数据和分数等
     * 计分标准为下落一个块1分，一次消一行10分、2行20分、3行40分、4行80分
     *
     * @param row 消掉的方块行数
     */
    public void updateDataAndUi(int row) {
        // 等级
        final int gradeUsed = Integer.parseInt(grade.getText().toString());
        // 消除行数
        final int rows = Integer.parseInt(rowNum.getText().toString()) + row;
        // 计算需要增加的分数
        int addScore = (int) Math.pow(2, row - 1) * 10;
        // 成绩=旧成绩+新添成绩
        final int scoreNew = Integer.parseInt(score.getText().toString()) + addScore;
        // 新等级
        final int gradeNew = computeGrade(gradeUsed, scoreNew);
        runOnUiThread(() -> {
            if (gradeUsed != gradeNew) {
                Toast.makeText(ClassicBlockActivity.this, "等级提升", Toast.LENGTH_SHORT).show();
                // 设置下落速度
                int newSpeed = speed - 200;
                if (newSpeed > 0) {
                    speed = newSpeed;
                } else {
                    // 改变加速规则 -50
                    int newSpeedTwo = speed - 50;
                    // 最快速度维持100
                    int maxSpeed = 100;
                    if (newSpeedTwo >= maxSpeed) {
                        speed = newSpeedTwo;
                    }
                }
                grade.setText(String.valueOf(gradeNew));
            }
            score.setText(String.valueOf(scoreNew));
            rowNum.setText(String.valueOf(rows));
        });
    }

    /**
     * 计算游戏等级
     * 100 400 800 1600
     *
     * @param grade 当前等级
     * @param score 当前分数
     * @return 应该处于的游戏等级
     */
    private int computeGrade(int grade, int score) {
        // 计算出来的成绩
        double computeScore = score / Math.pow(grade, 2);
        // 满分成绩
        int maxScore = 100;
        if ((int)computeScore >= maxScore) {
            return computeGrade(grade + 1, score);
        } else {
            return grade;
        }
    }

    /**
     * 创建游戏结束的弹窗
     */
    @Override
    public void gameOver() {
        super.gameOver();
        int maxScoreNew = Integer.parseInt(score.getText().toString());
        final String textContent;
        // 判断成绩是否需要保存
        if (maxScoreNew > maxScore) {
            textContent = "恭喜您打破记录，目前的成绩为：" + maxScoreNew + " 分";
            // 保存本地
            if (SharedPreferencesUtils.saveScore(maxScoreNew)) {
                LoggerUtils.i("[SP] 保存成绩失败");
            }
            // 上传游戏分数
            CacheManager.getInstance().put(ConstUtils.CACHE_WAIT_UPLOAD_CLASSIC_SCORE, maxScoreNew);
        } else {
            textContent = "别灰心，再来一次就突破！";
        }
        // 弹出弹窗
        runOnUiThread(() -> DialogUtils.showDialog(context, "游戏结束", textContent,
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
