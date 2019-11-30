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
import com.np.block.util.DialogUtils;
import com.np.block.view.NextTetrisView;
import com.np.block.view.TetrisView;

public class ClassicBlockActivity extends BaseActivity implements View.OnClickListener {
    // 设定GameActivity的code标识为1
    public static final int CODE = 1;
    // 自定义的俄罗斯方块视图
    private TetrisView tetris;
    // 自定义的下一个俄罗斯方块视图
    private NextTetrisView nextTetris;
    // 分数
    public TextView score;
    // 等级
    private TextView grade;
    // 消掉的行数
    public TextView rowNum;
    // 最高分
    private TextView maxScore;
    // 左移按钮
    private Button left;
    // 右移按钮
    private Button right;
    // 旋转按钮
    private Button rotate;
    // 下落按钮
    private Button down;
    // 暂停对话框
    private AlertDialog dialog = null;
    // 游戏结束对话框
    private AlertDialog dialogOver = null;

    @Override
    public void init() {
        // 获取对应的视图对象
        tetris = findViewById(R.id.tetrisView);
        nextTetris = findViewById(R.id.nextTetrisView);
        score = findViewById(R.id.score);
        grade = findViewById(R.id.grade);
        rowNum = findViewById(R.id.row_num);
        maxScore = findViewById(R.id.max_score);
        // 获取按钮对象
        left = findViewById(R.id.left);
        right = findViewById(R.id.right);
        down = findViewById(R.id.down);
        rotate = findViewById(R.id.rotate);
        // 设置按钮单击事件
        left.setOnClickListener(this);
        right.setOnClickListener(this);
        down.setOnClickListener(this);
        rotate.setOnClickListener(this);
        // 设置子视图对象的父视图
        tetris.setFatherActivity(this);
        nextTetris.setFather(this);
        // 开始游戏时设置一次下一个方块视图
        setNextTetrisView();
        // 启动下落线程
        tetris.startDownThread();
    }

    @Override
    public int getContentView() {
        return R.layout.activity_classic_block;
    }

    /**
     * 给下一个方块视图重新生成新方块
     */
    public void setNextTetrisView () {
        nextTetris.setNextTetris(tetris.getNextTetrisViewTetris());
    }

    /**
     * 更新游戏数据和分数等
     *
     * @param row 消掉的方块行数
     */
    public void updateDataAndUi(int row) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 加分 等级乘方块数乘10
                score.setText(Integer.parseInt(score.getText().toString()) + 400 + "");
                // 设置 消除行数
                rowNum.setText(Integer.parseInt(rowNum.getText().toString()) + 1 + "");
            }
        });
    }

    /**
     * 创建游戏结束的弹窗
     */
    public void startGameOverDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogUtils.showDialog(context, "游戏结束", "别灰心，再来一次就成功！",
                        "退出", "重来", false, false,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                finish();
                                ActivityManager.getInstance().removeActivity(ClassicBlockActivity.this);
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                setResult(RESULT_OK); finish();
                                ActivityManager.getInstance().removeActivity(ClassicBlockActivity.this);
                            }
                        });
            }
        });
    }

    /**
     * 创建暂停时的弹窗
     */
    private void startPauseDialog() {
        // 创建对话框构建器
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 获取布局
        View view = View.inflate(ClassicBlockActivity.this,R.layout.game_pause_dialog,null);
        // 获取布局中的控件
        Button homepage = view.findViewById(R.id.homepage);
        Button refresh = view.findViewById(R.id.refresh);
        Button keepGame = view.findViewById(R.id.keepGame);
        // 设置对话框参数
        builder.setTitle("请选择你的操作").setIcon(R.drawable.ic_launcher_foreground)
                .setView(view);
        // 创建对话框
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        // 设置按钮单击事件
        homepage.setOnClickListener(this);
        refresh.setOnClickListener(this);
        keepGame.setOnClickListener(this);
    }

    /**
     * 返回时触发
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if (dialog == null) {
                startPauseDialog();
            }
            tetris.startOrPauseDownThread(false);
            dialog.show();
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
            case R.id.down : tetris.toDown(); break;
            case R.id.rotate : tetris.toRotate(); break;
            case R.id.homepage : tetris.closeDownThread(); dialog.dismiss();finish();
                ActivityManager.getInstance().removeActivity(this); break;
            case R.id.refresh : tetris.closeDownThread(); dialog.dismiss();   setResult(RESULT_OK); finish();
                ActivityManager.getInstance().removeActivity(this); break;
            case R.id.keepGame : dialog.dismiss(); tetris.startOrPauseDownThread(true); break;
            default:
                Toast.makeText(context, "尚未实现", Toast.LENGTH_SHORT).show();
        }
    }
}
