package com.np.block.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.np.block.R;
import com.np.block.view.NextTetrisView;
import com.np.block.view.TetrisView;

public class GameActivity extends Activity implements View.OnClickListener {
    // 设定GameActivity的code标识为1
    public static final int CODE = 1;
    // 自定义的俄罗斯方块视图
    private TetrisView tetrisView;
    // 自定义的下一个俄罗斯方块视图
    private NextTetrisView nextTetrisView;
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
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        // 获取对应的视图对象
        tetrisView = findViewById(R.id.tetrisView);
        nextTetrisView = findViewById(R.id.nextTetrisView);
        score = findViewById(R.id.score);
        grade = findViewById(R.id.grade);
        rowNum = findViewById(R.id.rowNum);
        maxScore = findViewById(R.id.maxScore);
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
        // 设置视图对象的父类
        tetrisView.setFather(this);
        // 暂停 tetrisView.startOrPauseDownThread();
        // 开始游戏时设置一次下一个方块视图
        setNextTetrisView();
        // 启动下落线程
        tetrisView.startDownThread();
    }

    /**
     * 给下一个方块视图重新生成新方块
     */
    public void setNextTetrisView() {
        nextTetrisView.setNextTetris(tetrisView.getNextTetrisViewTetris());
    }

    /**
     * 创建暂停时的弹窗
     */
    private void createPauseDialog() {
        // 创建对话框构建器
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 获取布局
        View view = View.inflate(GameActivity.this,R.layout.game_pause_dialog,null);
        // 获取布局中的控件
        ImageButton homepage = view.findViewById(R.id.homepage);
        ImageButton refresh = view.findViewById(R.id.refresh);
        ImageButton keepGame = view.findViewById(R.id.keepGame);
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
            createPauseDialog();
            tetrisView.startOrPauseDownThread();
            dialog.show();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 主界面的单击事件
     * @param view 被单击对象
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left : tetrisView.toLeft(); break;
            case R.id.right : tetrisView.toRight(); break;
            case R.id.down : tetrisView.toDown(); break;
            case R.id.rotate : tetrisView.toRotate(); break;
            case R.id.homepage : tetrisView.colseDownThread(); dialog.dismiss(); finish(); break;
            case R.id.refresh : tetrisView.colseDownThread(); dialog.dismiss(); setResult(RESULT_OK); finish(); break;
            case R.id.keepGame : dialog.dismiss(); tetrisView.startOrPauseDownThread(); break;
            default:
                Toast.makeText(this, "尚未实现", Toast.LENGTH_SHORT).show();
        }
    }
}
