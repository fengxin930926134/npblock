package com.np.block.core.manager;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.np.block.R;
import com.np.block.adapter.TalkContentAdapter;
import com.np.block.core.model.TalkItem;
import com.np.block.core.model.Users;
import com.np.block.util.ConstUtils;
import com.np.block.util.DialogUtils;
import com.np.block.util.LoggerUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * 聊天消息管理类
 * @author fengxin
 */
public class MessageManager {
    /**当前user*/
    private Users user;
    /**未读消息数量*/
    private int unreadMessageCount;
    /**聊天消息RecyclerView*/
    private RecyclerView talkRecyclerView;
    /**消息弹窗*/
    private AlertDialog messageDialog = null;
    /**聊天消息适配器*/
    private TalkContentAdapter contentAdapter = new TalkContentAdapter(new ArrayList<>());
    /**存聊天数据 用户id为key*/
    private Map<String, List<MultiItemEntity>> multiItemEntityMap = new Hashtable<>();
    /**存tabView 用户id为key*/
    private Map<String, TextView> tabMap = new Hashtable<>();
    /**存好友信息map 用户id为key*/
    private Map<String, Users> friendMap = new HashMap<>();
    /**当前选中id*/
    private String userId = null;

    /**
     * 刷新聊天消息
     * @param messages messages
     */
    public void refreshMessage(List<EMMessage> messages) {
        String id = user.getId().toString();
        for (int i = 0; i < messages.size(); i++) {
            EMMessage emMessage = messages.get(i);
            String from = emMessage.getFrom();
            String to = emMessage.getTo();
            //等于当前用户
            if (from.equals(id)) {
                from = to;
            }
            //此时from是消息对方id 判断对方id是否是当前选中 是则刷新适配器
            if (this.userId != null && this.userId.equals(from)) {
                contentAdapter.notifyDataSetChanged();
                talkRecyclerView.smoothScrollToPosition(contentAdapter.getItemCount());
            }
        }
    }

    /**
     * 循环将消息加入消息map
     * @param messages messages
     */
    public void addMessage(List<EMMessage> messages) {
        //当前登录的userId
        String userId = user.getId().toString();
        //循环将消息加入消息map
        for (int i = 0; i < messages.size(); i++) {
            EMMessage emMessage = messages.get(i);
            String from = emMessage.getFrom();
            String to = emMessage.getTo();
            EMTextMessageBody txtBody = (EMTextMessageBody) emMessage.getBody();
            TalkItem talkItem;
            if (userId.equals(from)) {
                from = to;
                talkItem = new TalkItem(user.getHeadSculpture(), txtBody.getMessage(), TalkItem.TYPE_RIGHT);
            } else if (userId.equals(to)) {
                Users users = friendMap.get(from);
                String headSculpture = "";
                if (users != null && users.getHeadSculpture() != null) {
                    headSculpture = user.getHeadSculpture();
                }
                talkItem = new TalkItem(headSculpture, txtBody.getMessage(), TalkItem.TYPE_LEFT);
            } else {
                continue;
            }
            List<MultiItemEntity> itemEntities = multiItemEntityMap.get(from);
            if (itemEntities == null) {
                itemEntities = new ArrayList<>();
                multiItemEntityMap.put(from, itemEntities);
            }
            itemEntities.add(talkItem);
        }
    }

    /**
     * 弹出消息弹窗包括存在的会话
     * 以及未读消息
     *
     * @param context context
     * @param toChatUsername 环信username 即usersId 不为空则默认按下此idTab
     */
    public void showMessageDialog(Activity context, String toChatUsername) {
        if (toChatUsername != null && !multiItemEntityMap.containsKey(toChatUsername)) {
            //此时是增加一条会话
            multiItemEntityMap.put(toChatUsername, new ArrayList<>());
        }
        messageDialog = DialogUtils.showDialogDefault(context);
        View inflate = View.inflate(context, R.layout.alert_dialog_talk, null);
        inflate.findViewById(R.id.alert_finish).setOnClickListener(v -> {
            messageDialog.cancel();
            messageDialog = null;
        });
        Button send = inflate.findViewById(R.id.send);
        //开启未选中会话提示
        TextView tips = inflate.findViewById(R.id.none_content);
        tips.setVisibility(View.VISIBLE);
        send.setEnabled(false);
        //发送消息内容
        talkRecyclerView = inflate.findViewById(R.id.talk_recyclerView);
        EditText editText = inflate.findViewById(R.id.edit_send);
        send.setOnClickListener(v -> {
            String text = editText.getText().toString();
            if (TextUtils.isEmpty(text)) {
                Toast.makeText(context, "消息不能为空", Toast.LENGTH_SHORT).show();
            } else {
                if (userId == null) {
                    Toast.makeText(context, "发送失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id
                EMMessage message = EMMessage.createTxtSendMessage(text, userId);
                //发送消息
                message.setMessageStatusCallback(new EMCallBack(){
                    @Override
                    public void onSuccess() {
                        LoggerUtils.i("消息发送成功");
                        contentAdapter.getData().add(new TalkItem(user.getHeadSculpture(), text, TalkItem.TYPE_RIGHT));
                        contentAdapter.notifyItemInserted(contentAdapter.getItemCount());
                        //滚动到底部
                        talkRecyclerView.smoothScrollToPosition(contentAdapter.getItemCount());
                        context.runOnUiThread(() -> {
                            //清空输入框消息
                            editText.getText().clear();
                        });
                    }

                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(context, "请检查网络，消息发送失败", Toast.LENGTH_SHORT).show();
                        LoggerUtils.i("消息发送失败");
                    }

                    @Override
                    public void onProgress(int i, String s) {
                        LoggerUtils.i("消息发送onProgress");
                    }
                });
                EMClient.getInstance().chatManager().sendMessage(message);
            }
        });
        //循环添加聊天会话tab
        LinearLayout talkHeader = inflate.findViewById(R.id.talk_header);
        for (String userId : multiItemEntityMap.keySet()) {
            //该方法如果设置最后一个参数为false，那么布局会加载xml的属性，不受ViewGroup影响
            View view = LayoutInflater.from(context).inflate(R.layout.talk_tab_item, talkHeader, false);
            //设置tab名称
            TextView tab = view.findViewById(R.id.talk_tab_name);
            Users friend = friendMap.get(userId);
            if (friend != null && friend.getGameName() != null) {
                tab.setText(friend.getGameName());
            } else {
                tab.setText("无");
            }
            view.findViewById(R.id.talk_tab_close).setOnClickListener(v -> {
                send.setEnabled(false);
                contentAdapter.setNewData(null);
                talkHeader.removeView(view);
                //开启未选中会话提示
                tips.setVisibility(View.VISIBLE);
                deleteConversation(userId);
            });
            view.setOnClickListener(v -> {
                send.setEnabled(true);
                //设置当前UserId
                this.userId = userId;
                //关闭未选中会话提示
                tips.setVisibility(View.INVISIBLE);
                for (String id :tabMap.keySet()) {
                    TextView textView = tabMap.get(id);
                    if (textView == null) {
                        continue;
                    }
                    if (!id.equals(userId)) {
                        //没点击的
                        textView.setBackgroundResource (R.drawable.talk_tab_background);
                    } else {
                        textView.setBackgroundResource (R.drawable.talk_tab_background_down);
                    }
                }
                contentAdapter.setNewData(multiItemEntityMap.get(userId));
            });
            tabMap.put(userId, tab);
            talkHeader.addView(view);
        }
        // 设置布局管理器
        talkRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        // 设置adapter适配器
        talkRecyclerView.setAdapter(contentAdapter);
        // 设置刷新相关属性
        talkRecyclerView.setHasFixedSize(true);
        // 设置成白板数据
        contentAdapter.setNewData(null);
        messageDialog.setContentView(inflate);
        //如果不为空则自动选中
        if (toChatUsername != null) {
            this.userId = toChatUsername;
            send.setEnabled(true);
            //关闭未选中会话提示
            tips.setVisibility(View.INVISIBLE);
            TextView textView = tabMap.get(toChatUsername);
            if (textView != null) {
                textView.setBackgroundResource (R.drawable.talk_tab_background_down);
            }
            contentAdapter.setNewData(multiItemEntityMap.get(toChatUsername));
        }
    }

    /**
     * 根据userId 删除会话
     * @param userId userId
     */
    private void deleteConversation(String userId) {
        tabMap.remove(userId);
        multiItemEntityMap.remove(userId);
        //删除和某个user会话，如果需要保留聊天记录，传false
        EMClient.getInstance().chatManager().deleteConversation(userId, false);
    }

    /**
     * 初始化所有会话消息
     */
    private void initConversations() {
        //会话map
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        LoggerUtils.i("所有会话=" + conversations.size());
        for (EMConversation emConversation : conversations.values()) {
            List<EMMessage> allMessages = emConversation.getAllMessages();
            //循环将消息加入消息map
            addMessage(allMessages);
        }
    }

    /**
     * 初始化好友信息map
     */
    private void initFriendMap(){
        List<Users> users = CacheManager.getInstance().getUsers(ConstUtils.CACHE_USER_FRIEND_INFO);
        if (users != null) {
            for (Users user: users) {
                friendMap.put(user.getId().toString(), user);
            }
        }
    }

    /**提供一个共有的可以返回类对象的方法*/
    public static MessageManager getInstance(){
        return Inner.instance;
    }

    /**私有化内部类 第一次加载类时初始化CacheManager*/
    private static class Inner {
        private static MessageManager instance = new MessageManager();
    }

    private MessageManager(){
        user = (Users) CacheManager.getInstance().get(ConstUtils.CACHE_USER_INFO);
        unreadMessageCount = EMClient.getInstance().chatManager().getUnreadMessageCount();
        //初始化好友信息map
        initFriendMap();
        //初始化已存在会话
        initConversations();
    }
}
