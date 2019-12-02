package com.np.block.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.np.block.R;
import com.np.block.base.BaseActivity;
import com.np.block.core.manager.ActivityManager;
import com.np.block.core.manager.ThreadPoolManager;
import com.np.block.core.model.Tetris;
import com.np.block.util.DialogUtils;
import com.np.block.view.NextTetrisView;
import com.np.block.view.TetrisView;

/**
 * 经典模式
 * @author fengxin
 */
public class ClassicBlockActivity extends BaseActivity implements View.OnClickListener {
    /**俄罗斯方块视图*/
    private TetrisView tetris;
    /**下一个俄罗斯方块视图*/
    private NextTetrisView nextTetris;
    /**分数*/
    private TextView score;
    /**等级*/
    private TextView grade;
    /**消掉的行数*/
    private TextView rowNum;
    /**最高分*/
    private TextView maxScore;
    /**暂停对话框*/
    private AlertDialog pauseDialog = null;
    /**判断是否长点击*/
    public boolean isLongClick = false;
    /**下落的速度*/
    private int speed = 1000;
    /**标识游戏是暂停还是运行*/
    private boolean runningStatus = true;
    /**标识游戏是开始还是结束*/
    private boolean beginGame = true;

    @Override
    public void init() {
        // 获取对应的视图对象
        tetris = findViewById(R.id.tetrisView);
        nextTetris = findViewById(R.id.nextTetrisView);
        score = findViewById(R.id.score);
        grade = findViewById(R.id.grade);
        rowNum = findViewById(R.id.row_num);
        maxScore = findViewById(R.id.max_score);
        // 初始化数据
        score.setText("0");
        grade.setText("1");
        rowNum.setText("0");
        maxScore.setText("0");
        // 左移按钮
        Button left = findViewById(R.id.left);
        // 右移按钮
        Button right = findViewById(R.id.right);
        // 下落按钮
        Button down = findViewById(R.id.down);
        // 旋转按钮
        Button rotate = findViewById(R.id.rotate);
        // 设置按钮单击事件
        left.setOnClickListener(this);
        right.setOnClickListener(this);
        down.setOnClickListener(this);
        rotate.setOnClickListener(this);
        down.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //启动长按下落线程
                startDownLongThread();
                // 如果让回调消耗该长按，返回true，否则false，如果false，其他地方监听生效
                return false;
            }
        });
        // 设置子视图对象的父视图
        tetris.setFatherActivity(this);
        // 启动下落线程  咳咳  游戏启动
        startDownThread();
    }

    @Override
    public int getContentView() {
        return R.layout.activity_classic_block;
    }

    /**
     * 从下一个方块视图里获取方块
     */
    public Tetris getNextTetris() {
        return nextTetris.getNextTetris();
    }

    /**
     * 更新游戏数据和分数等
     *
     * @param row 消掉的方块行数
     */
    public void updateDataAndUi(int row) {
        // 等级
        final int gradeUsed = Integer.parseInt(grade.getText().toString());
        // 成绩=等级x方块数
        final int scoreNew = Integer.parseInt(score.getText().toString()) + row * TetrisView.COLUMN_NUM * gradeUsed;
        // 消除行数
        final int rows = Integer.parseInt(rowNum.getText().toString()) + row;
        // 新等级
        final int gradeNew = computeGrade(gradeUsed, scoreNew);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (gradeUsed != gradeNew) {
                    Toast.makeText(ClassicBlockActivity.this, "等级提升", Toast.LENGTH_SHORT).show();
                    //设置下落速度
                    int newSpeed = speed - 200;
                    if (newSpeed != 0) {
                        speed = newSpeed;
                    }
                    grade.setText(String.valueOf(gradeNew));
                }
                score.setText(String.valueOf(scoreNew));
                rowNum.setText(String.valueOf(rows));
            }
        });
    }

    /**
     * 计算游戏等级
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
    private void startGameOverDialog() {
        beginGame = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogUtils.showDialog(context, "游戏结束", "别灰心，再来一次就成功！",
                        "回到主页", "重来", false, false,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                exitGame();
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(ClassicBlockActivity.this, "尚未实现", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    /**
     * 创建暂停时的弹窗
     */
    private void startPauseDialog() {
        // 暂停下落
        runningStatus = false;
        pauseDialog = DialogUtils.showDialog(context, "游戏暂停", "客官，继续来呗！",
                "回到主页", "重来", "继续", false, true,
                //回到主页
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        exitGame();
                    }
                },
                //刷新重来
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ClassicBlockActivity.this, "尚未实现", Toast.LENGTH_SHORT).show();
                    }
                },
                //继续游戏
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        runningStatus = true;
                        pauseDialog = null;
                    }
                },
                //重写返回键事件
                new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (event.getAction() == KeyEvent.ACTION_DOWN
                                && keyCode == KeyEvent.KEYCODE_BACK
                                && event.getRepeatCount() == 0) {
                            dialog.cancel();
                            runningStatus = true;
                            pauseDialog = null;
                        }
                        return true;
                    }
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
     * 主界面的单击事件
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
     * 启动下落游戏线程
     */
    private void startDownThread () {
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                while (beginGame) {
                    if (runningStatus) {
                        //下移
                        down();
                        try {
                            Thread.sleep(speed);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
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
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                do {
                    down();
                    try {
                        Thread.sleep(80);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (isLongClick && beginGame);
            }
        });
    }

    /**
     * 在ui线程中执行方块下落
     */
    private synchronized void down() {
        //下移
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (tetris.toDown()) {
                    // 启动游戏结束的弹窗
                    startGameOverDialog();
                }
            }
        });
    }
}
