package com.np.block.base;

import android.widget.Button;
import com.np.block.R;
import com.np.block.core.manager.ThreadPoolManager;
import com.np.block.core.model.Tetris;
import com.np.block.view.NextTetrisView;

public abstract class BaseGameActivity extends BaseActivity{
    private BaseTetrisView tetrisView;
    /**下一个俄罗斯方块视图*/
    private NextTetrisView nextTetris;
    /**判断是否长点击*/
    public boolean isLongClick = false;
    /**默认下落的速度*/
    public int speed = 1000;
    /**默认长按的下落的速度*/
    public int speedLong = 70;
    /**标识游戏是暂停还是运行*/
    public boolean runningStatus = true;
    /**标识游戏是开始还是结束*/
    public boolean beginGame = true;

    /**
     * 获取游戏主视图的view
     *
     * @return view
     */
    public abstract BaseTetrisView getTetrisView();

    /**
     * 初始化数据
     */
    public abstract void initData();

    @Override
    public void init() {
        // 获取对应的视图对象
        tetrisView = getTetrisView();
        nextTetris = findViewById(R.id.nextTetrisView);
        // 左移按钮
        findViewById(R.id.left).setOnClickListener(v -> leftEvent());
        // 右移按钮
        findViewById(R.id.right).setOnClickListener(v -> rightEvent());
        // 旋转按钮
        findViewById(R.id.rotate).setOnClickListener(v -> rotateEvent());
        // 下落按钮
        Button down = findViewById(R.id.down);
        down.setOnClickListener(v -> downEvent());
        down.setOnLongClickListener(v -> {
            //启动长按下落线程
            startDownLongThread();
            // 如果让回调消耗该长按，返回true，否则false，如果false，其他地方监听生效
            return false;
        });
        // 设置子视图对象的父视图
        tetrisView.setFatherActivity(this);
        initData();
    }

    /**
     * 从下一个方块视图里获取方块
     */
    public Tetris getNextTetris() {
        return nextTetris.getNextTetris();
    }

    /**
     * 创建游戏结束的弹窗
     */
    public void startGameOverDialog() {
        beginGame = false;
    }

    /**
     * 下落按钮的点击事件
     */
    public void downEvent() {
        if (!isLongClick && beginGame) {
            if (tetrisView.toDown()) {
                beginGame = false;
            }
        }else {
            isLongClick = false;
        }
    }

    /**
     * 右移按钮的点击事件
     */
    public void rightEvent() {
        tetrisView.toRight();
    }

    /**
     * 左移按钮的点击事件
     */
    public void leftEvent() {
        tetrisView.toLeft();
    }

    /**
     * 旋转按钮的点击事件
     */
    public void rotateEvent() {
        tetrisView.toRotate();
    }

    /**
     * 启动下落游戏线程
     */
    public void startDownThread () {
        ThreadPoolManager.getInstance().execute(() -> {
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
                    Thread.sleep(speedLong);
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
            if (tetrisView.toDown()) {
                // 启动游戏结束的弹窗
                startGameOverDialog();
            }
        });
    }
}
