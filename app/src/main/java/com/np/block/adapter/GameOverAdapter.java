package com.np.block.adapter;

import android.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.np.block.R;
import com.np.block.core.enums.SexTypeEnum;
import com.np.block.core.manager.CacheManager;
import com.np.block.core.model.Users;
import com.np.block.util.ConstUtils;
import com.np.block.util.DialogUtils;
import com.np.block.util.LoggerUtils;
import java.util.List;

/**
 *  游戏结束适配器
 *
 * @author fengxin
 */
public class GameOverAdapter extends BaseQuickAdapter<Users, BaseViewHolder> {

    /**头像属性配置*/
    private RequestOptions options =new RequestOptions()
            //加载成功之前占位图
            .placeholder(R.mipmap.np_block_launcher)
            //加载错误之后的错误图
            .error(R.mipmap.np_block_launcher)
            //指定图片的尺寸
            .override(50,50)
            //指定图片的缩放类型为fitCenter （等比例缩放图片，宽或者是高等于ImageView的宽或者是高。）
            .fitCenter()
            //指定图片的缩放类型为centerCrop （等比例缩放图片，直到图片的狂高都大于等于ImageView的宽度，然后截取中间的显示。）
            .centerCrop()
            //指定图片的缩放类型为centerCrop （圆形）
            .circleCrop();

    /**读取当前用户信息*/
    private Users users;
    /**读取当前用户好友信息*/
    private List<Users> userFriends = null;

    public GameOverAdapter(int layoutResId, List<Users> data) {
        super(layoutResId, data);
        if (CacheManager.getInstance().containsUsers(ConstUtils.CACHE_USER_FRIEND_INFO)){
            userFriends = CacheManager.getInstance().getUsers(ConstUtils.CACHE_USER_FRIEND_INFO);
        }
        Object o = CacheManager.getInstance().get(ConstUtils.CACHE_USER_INFO);
        if (o instanceof Users){
            users = (Users) o;
        }
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Users item) {
        //加载头像
        ImageView rankItemImg = helper.getView(R.id.item_img);
        Glide.with(mContext)
                .load(item.getHeadSculpture())
                .apply(options)
                .into(rankItemImg);
        //设置性别
        if (item.getSex() != null && SexTypeEnum.getEnumByCode(item.getSex()).equals(SexTypeEnum.FEMALE)) {
            helper.setBackgroundRes(R.id.sex, R.mipmap.girl);
        }
        //设置事件
        rankItemImg.setOnClickListener(v -> Toast.makeText(mContext, "没有任何实现", Toast.LENGTH_SHORT).show());
        //设置添加好友事件
        boolean isFriend = false;
        if (userFriends != null) {
            for (int i = 0; i < userFriends.size(); i++) {
                Users users = userFriends.get(i);
                if (users.getId().equals(item.getId())) {
                    isFriend = true;
                    break;
                }
            }
        }
        //设置添加好友事件
        if (!isFriend) {
            helper.getView(R.id.add_friend).setOnClickListener(v -> addUserDialog(item));
        } else {
            helper.getView(R.id.add_friend).setOnClickListener(v ->
                    Toast.makeText(mContext, "请勿重复添加", Toast.LENGTH_SHORT).show());
        }
        //设置名称 自己是金黄
        helper.setText(R.id.user_name, item.getGameName());
        if (item.getId().equals(users.getId())) {
            helper.setTextColor(R.id.user_name, ResourcesCompat.getColor(mContext.getResources(), R.color.golden, null));
            //移除是自己的添加好友按钮
            helper.getView(R.id.add_friend).setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 添加用户弹窗
     */
    private void addUserDialog(Users user) {
        AlertDialog alertDialog = DialogUtils.showDialogDefault(mContext);
        View view = View.inflate(mContext, R.layout.alert_dialog_select_input, null);
        //标题
        TextView tvTitle = view.findViewById(R.id.tv_alert_title);
        tvTitle.setText("添加好友");
        //内容
        EditText tvContent = view.findViewById(R.id.tv_alert_content);
        tvContent.setHint("输入添加好友的原因");
        tvContent.setText("来不及解释是因为爱情了");
        //取消按钮
        Button buttonCancel = view.findViewById(R.id.btn_alert_cancel);
        buttonCancel.setText("取消");
        buttonCancel.setOnClickListener(v -> alertDialog.cancel());
        //确定按钮
        Button buttonOk = view.findViewById(R.id.btn_alert_ok);
        buttonOk.setText("确认");
        buttonOk.setOnClickListener(v -> {
            //参数为要添加的好友的username和添加理由
            try {
                EMClient.getInstance().contactManager().addContact(String.valueOf(user.getId()), tvContent.getText().toString());
                alertDialog.cancel();
                Toast.makeText(mContext, "发送成功", Toast.LENGTH_SHORT).show();
            } catch (HyphenateException e) {
                LoggerUtils.e(e.getMessage());
                Toast.makeText(mContext, "发送失败", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.setContentView(view);
    }
}