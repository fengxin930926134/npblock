package com.np.block.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSONObject;
import com.np.block.R;
import com.np.block.base.BaseActivity;
import com.np.block.core.manager.CacheManager;
import com.np.block.core.manager.ThreadPoolManager;
import com.np.block.core.model.Users;
import com.np.block.util.ConstUtils;
import com.np.block.util.DialogUtils;
import com.np.block.util.FormatConversionUtils;
import com.np.block.util.OkHttpUtils;
import com.np.block.util.VerificationUtils;
import butterknife.BindView;

/**
 * 输入名字的界面
 * @author fengxin
 */
public class InputNameActivity extends BaseActivity {

    @BindView(R.id.name_input_title)
    TextView title;
    @BindView(R.id.name_input_bg)
    ImageView nameBackground;
    /**创建角色*/
    @BindView(R.id.submit_name)
    Button submitName;
    /**输入名字的输入框*/
    @BindView(R.id.input_name)
    EditText inputName;
    /**用户信息*/
    private Users users;

    @Override
    public void init() {
        users = (Users) CacheManager.getInstance().get(ConstUtils.CACHE_USER_INFO);
        //对图片模糊化处理
        Bitmap bitmap = FormatConversionUtils.blurBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.lou_ti));
        nameBackground.setImageBitmap(bitmap);
        //将图片等比例缩放，让图像的短边与ImageView的边长度相同，即不能留有空白，缩放后截取中间部分进行显示
        nameBackground.setScaleType(ImageView.ScaleType. CENTER_CROP);
        String str6 = "<font color=\"#CD7F32\">小块块 </font><font color=\"#000000\">请告诉我</font><font color=\"#CD7F32\">你的名字</font>";
        title.setText(Html.fromHtml(str6));
        submitName.setOnClickListener( v -> {
            String name = inputName.getText().toString();
            // 验证游戏名
            if (VerificationUtils.validateGameName(name)) {
                AlertDialog dialog = DialogUtils.showDialog(context);
                //请求服务器修改
                ThreadPoolManager.getInstance().execute(() -> {
                    // 设置请求参数
                    Users user = new Users();
                    user.setToken(users.getToken());
                    user.setGameName(name);
                    try {
                        JSONObject response = OkHttpUtils.post("/user/gameName", JSONObject.toJSONString(user));
                        if (response.getIntValue(ConstUtils.CODE) == ConstUtils.CODE_SUCCESS) {
                            //保存游戏名称成功 更新缓存
                            users.setGameName(name);
                            CacheManager.getInstance().put(ConstUtils.CACHE_USER_INFO, users);
                            runOnUiThread(dialog::cancel);
                            startActivity(new Intent(context, MainActivity.class));
                        } else {
                            throw new Exception(response.getString(ConstUtils.MSG));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show());
                    }finally {
                        runOnUiThread(dialog::cancel);
                    }
                });

            }
        });
    }

    @Override
    public int getContentView() {
        return R.layout.activity_input_name;
    }
}
