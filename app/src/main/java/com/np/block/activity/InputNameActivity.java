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
        Bitmap bitmap = FormatConversionUtils.blurBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.name_input_bg));
        nameBackground.setImageBitmap(bitmap);
        //将图片等比例缩放，让图像的短边与ImageView的边长度相同，即不能留有空白，缩放后截取中间部分进行显示
        nameBackground.setScaleType(ImageView.ScaleType. CENTER_CROP);
        String str6 = "<font color=\"#CD7F32\">小块块 </font><font color=\"#000000\">请告诉我</font><font color=\"#CD7F32\">你的名字</font>";
        title.setText(Html.fromHtml(str6));
        submitName.setOnClickListener( v -> {
            String name = inputName.getText().toString();
            users.setGameName(name);
            // 验证游戏名
            if (VerificationUtils.validateGameName(name)) {
                AlertDialog dialog = DialogUtils.showDialog(context);
                //请求服务器修改
                ThreadPoolManager.getInstance().execute(() -> {
                    try {
                        JSONObject response = OkHttpUtils.post("/user/gameName", JSONObject.toJSONString(users));
                        int intValue = response.getIntValue(ConstUtils.CODE);
                        if (intValue == ConstUtils.CODE_ERROR) {
                            runOnUiThread(() -> DialogUtils.showTextDialog(context, null, "您的名字被取啦！！"));
                        } else if (intValue == ConstUtils.CODE_SUCCESS) {
                            //保存游戏名称成功
                            //更新缓存
                            CacheManager.getInstance().put(ConstUtils.CACHE_USER_INFO, users);
                            startActivity(new Intent(context, MainActivity.class));
                        } else {
                            throw new Exception("更改数据库错误");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(context, "网络异常，请检查网络", Toast.LENGTH_SHORT).show());
                    }finally {
                        dialog.dismiss();
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
