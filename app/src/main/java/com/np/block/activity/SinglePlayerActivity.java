package com.np.block.activity;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.np.block.R;
import com.np.block.base.BaseActivity;

public class SinglePlayerActivity extends BaseActivity {

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return false;
        }
    });

    @Override
    public void init() {

    }

    @Override
    public int getContentView() {
        return R.layout.activity_single_player;
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
