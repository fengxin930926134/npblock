package com.np.block;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;
import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.exceptions.HyphenateException;
import com.np.block.activity.MainActivity;
import com.np.block.core.manager.ActivityManager;
import com.np.block.core.manager.CacheManager;
import com.np.block.core.model.Users;
import com.np.block.util.ConstUtils;
import com.np.block.util.LoggerUtils;
import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;
import java.util.ArrayList;
import java.util.List;

/**
 * 主Application
 * @author fengxin
 */
public class NpBlockApplication extends Application {

    private static NpBlockApplication app;
    /**是否收到好友申请*/
    public boolean receiveApplication = false;
    /**好友申请被同意*/
    public boolean applicationAgree = false;
    /**好友申请*/
    public List<Users> receiveApplyData = new ArrayList<>();
    /**全局mHandler*/
    public Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            super.handleMessage(msg);
            mListener.handleMessage(msg);
        }
    };
    /**全局mHandler监听器*/
    public HandlerListener mListener;
    public void setOnHandlerListener(HandlerListener listener) {
        mListener = listener;
    }

    public interface HandlerListener {
        /**
         * 接收消息
         * @param msg msg
         */
        void handleMessage(Message msg);
    }

    public static NpBlockApplication getInstance() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        // 进行一些全局的初始化操作
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        // 是否自动将消息附件上传到环信服务器，默认为True是使用环信服务器上传下载，如果设为 false，需要开发者自己处理附件消息的上传和下载
        options.setAutoTransferMessageAttachments(true);
        // 是否自动下载附件类消息的缩略图等，默认为 true 这里和上边这个参数相关联
        options.setAutoDownloadThumbnail(true);
        // 关闭自动登陆
        options.setAutoLogin(false);
        // 初始化
        EMClient.getInstance().init(getApplicationContext(), options);
        // 默认好友请求是自动同意的，设置为手动同意
        EMClient.getInstance().getOptions().setAcceptInvitationAlways(false);
        //初始化LitePal 不用一直传递Context参数，简化API
        LitePal.initialize(this);
        initContactListener();
        try {
            //开启接受离线通知  这样就能接收到离线消息了
            EMClient.getInstance().pushManager().enableOfflinePush();
        } catch (HyphenateException e) {
            LoggerUtils.e("开启离线消息失败：" + e.getMessage());
        }
    }

    /**
     * 初始化环信监听器
     */
    private void initContactListener() {
        EMClient.getInstance().contactManager().setContactListener(new EMContactListener() {
            @Override
            public void onContactInvited(String username, String reason) {
                LoggerUtils.i("收到好友邀请");
                receiveApplication = true;
                if (username != null) {
                    Users users = new Users();
                    users.setId(Integer.valueOf(username));
                    users.setReason(reason);
                    receiveApplyData.add(users);
                }
                //打开社交通知
                MainActivity activity = (MainActivity) ActivityManager.getInstance().getActivity(MainActivity.class);
                if (activity != null) {
                    activity.checkNotification();
                }
            }

            @Override
            public void onFriendRequestAccepted(String s) {
                LoggerUtils.i("好友请求被同意");
                applicationAgree = true;
                //后期用邮件说明
                //打开社交通知
                MainActivity activity = (MainActivity) ActivityManager.getInstance().getActivity(MainActivity.class);
                if (activity != null) {
                    activity.checkNotification();
                }
            }

            @Override
            public void onFriendRequestDeclined(String s) {
                LoggerUtils.i("好友请求被拒绝");
                Toast.makeText(getApplicationContext(), "好友申请被拒绝", Toast.LENGTH_SHORT).show();
                //后期用邮件说明
            }

            @Override
            public void onContactDeleted(String username) {
                LoggerUtils.i("被删除时回调此方法");
                CacheManager.getInstance().removeUsers(ConstUtils.CACHE_USER_FRIEND_INFO);
            }

            @Override
            public void onContactAdded(String username) {
                LoggerUtils.i("增加了联系人时回调此方法");
                CacheManager.getInstance().removeUsers(ConstUtils.CACHE_USER_FRIEND_INFO);
            }
        });
    }
}
