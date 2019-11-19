package com.np.block.util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import com.np.block.R;
import java.util.Objects;
import static com.np.block.util.ResolutionUtils.dpChangePx;

/**
 * 弹窗工具类
 * @author fengxin
 */
public class DialogUtils {

    /**
     * 创建两个按钮的弹窗
     *
     * @param context        上下文
     * @param title          标题
     * @param content        内容
     * @param btnCancelText  取消按钮文本
     * @param btnSureText    确定按钮文本
     * @param touchOutside   外部取消
     * @param cancelable     返回键取消
     * @param cancelListener 取消监听
     * @param sureListener   确定监听
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public synchronized static void showDialog(Context context,
                                                      String title,
                                                      String content,
                                                      String btnCancelText,
                                                      String btnSureText,
                                                      boolean touchOutside,
                                                      boolean cancelable,
                                                      DialogInterface.OnClickListener cancelListener,
                                                      DialogInterface.OnClickListener sureListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.create();
        // 设置点击dialog的外部能否取消弹窗
        dialog.setCanceledOnTouchOutside(touchOutside);
        // 设置能不能返回键取消弹窗
        dialog.setCancelable(cancelable);
        View view = View.inflate(context, R.layout.alert_dialog_select, null);
        //标题
        TextView tvTitle = view.findViewById(R.id.tv_alert_title);
        //内容
        TextView tvContent = view.findViewById(R.id.tv_alert_content);
        //取消按钮
        Button buttonCancle = view.findViewById(R.id.btn_alert_cancel);
        //确定按钮
        Button buttonOk = view.findViewById(R.id.btn_alert_ok);
        //线
        View viewLine = view.findViewById(R.id.v_alert_line);
        //判断标题是否为空
        if (TextUtils.isEmpty(title)) {
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setText(title);
        }
        tvContent.setText(TextUtils.isEmpty(content) ? "" : content);

        if (TextUtils.isEmpty(btnCancelText)) {
            buttonCancle.setVisibility(View.GONE);
            viewLine.setVisibility(View.GONE);
        } else {
            buttonCancle.setText(btnCancelText);
        }
        buttonOk.setText(TextUtils.isEmpty(btnSureText) ? "确定" : btnSureText);
        final AlertDialog dialogFinal = dialog;
        final DialogInterface.OnClickListener finalCancelListener = cancelListener;
        final DialogInterface.OnClickListener finalSureListener = sureListener;
        buttonCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalCancelListener.onClick(dialogFinal, DialogInterface.BUTTON_NEGATIVE);
            }
        });
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalSureListener.onClick(dialogFinal, DialogInterface.BUTTON_POSITIVE);
            }
        });
        dialog.show();
        //设置背景透明,去四个角
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.color.colorTransparent);
        //设置固定宽带，高度自适应
        int dialogWidth = 290;
        dialog.getWindow().setLayout(dpChangePx(context, dialogWidth), LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(view);
    }

    /**
     * 小圆圈弹窗
     *
     * @param context 上下文
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public synchronized static AlertDialog showDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.create();
        // 设置点击dialog的外部能否取消弹窗
        dialog.setCanceledOnTouchOutside(false);
        // 设置能不能返回键取消弹窗
        dialog.setCancelable(false);
        View view = View.inflate(context, R.layout.alert_dialog_round, null);
        dialog.show();
        //设置背景透明,去四个角
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.color.colorTransparent);
        //设置固定宽带，高度自适应
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        view.bringToFront();
        dialog.setContentView(view);
        return dialog;
    }

    /**
     * 默认弹窗
     *  @param context        上下文
     *
     * @return View
     */
    @SuppressLint("SetJavaScriptEnabled")
    public synchronized static AlertDialog showDialogDefault(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        //设置背景透明,去四个角
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        dialog.getWindow().setLayout(dpChangePx(context, 290), LinearLayout.LayoutParams.WRAP_CONTENT);
        //AlertDialog默认设置了WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM这个标志，所以键盘不会显示
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        return dialog;
    }

}
