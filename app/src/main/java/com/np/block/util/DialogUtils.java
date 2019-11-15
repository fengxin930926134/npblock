package com.np.block.util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
     * 登陆弹窗
     *  @param context        上下文
     *
     * @return View
     */
    @SuppressLint("SetJavaScriptEnabled")
    public synchronized static AlertDialog showDialogLogin(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        //设置背景透明,去四个角
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        dialog.getWindow().setLayout(dpChangePx(context, 290), LinearLayout.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

//    /**
//     * @param context        上下文
//     * @param title          顶部标题
//     * @param webUrl         网页的url
//     * @param btnText        按钮的文字
//     * @param checkText      CheckBox的文字
//     * @param touchOutside   点击外部取消
//     * @param sureListener   确定按钮的点击事件
//     * @param cancleListener 取消按钮的点击事件
//     * @param checkListener  checkbox的点击事件
//     * @return
//     */
//    @SuppressLint("SetJavaScriptEnabled")
//    public synchronized static AlertDialog showDialogXieYi(Context context,
//                                                           String title,
//                                                           String webUrl,
//                                                           String btnText,
//                                                           String checkText,
//                                                           boolean touchOutside,
//                                                           DialogInterface.OnClickListener cancleListener,
//                                                           DialogInterface.OnClickListener sureListener,
//                                                           DialogInterface.OnMultiChoiceClickListener checkListener) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        AlertDialog dialog = builder.create();
//        dialog.setCanceledOnTouchOutside(touchOutside);
//        dialog.setCancelable(false);
//
//        // 是否包含标题，设置Title
//        if (TextUtils.isEmpty(title)) {
//            title = "提示";
//        }
//        View view = View.inflate(context, R.layout.alert_dialog_login, null);
//        //提示框title
//        TextView tvTitle = view.findViewById(R.id.alert_tv_title);
//        //网页webView
//        WebView webView = view.findViewById(R.id.alert_wv);
//        //按钮
//        final Button button = view.findViewById(R.id.alert_btn);
//        //CheckBox的说明文字
//        TextView tvCheck = view.findViewById(R.id.alert_tv_check);
//        //finish按钮
//        ImageView imageView = view.findViewById(R.id.alert_iv_finish);
//        //协议选中框
//        CheckBox checkBox = view.findViewById(R.id.alert_cb);
//
//        tvTitle.setText(title);
//        button.setText(TextUtils.isEmpty(btnText) ? "确定" : btnText);
//        tvCheck.setText(TextUtils.isEmpty(checkText) ? "" : checkText);
//        webView.setWebViewClient(new WebViewClient());
//        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        //设置webView里字体大小
//        WebSettings settings = webView.getSettings();
//        settings.setTextZoom(55);
//        settings.setJavaScriptEnabled(true);
//        settings.setSupportZoom(true);
//        settings.setBuiltInZoomControls(true);
//        webView.loadUrl(webUrl);
//        final AlertDialog dialogFinal = dialog;
//        final DialogInterface.OnClickListener finalSureListener = sureListener;
//        final DialogInterface.OnClickListener finalCancleListener = cancleListener;
//        final DialogInterface.OnMultiChoiceClickListener finalCheckListener = checkListener;
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finalSureListener.onClick(dialogFinal, DialogInterface.BUTTON_POSITIVE);
//            }
//        });
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finalCancleListener.onClick(dialogFinal, DialogInterface.BUTTON_NEGATIVE);
//            }
//        });
//        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                finalCheckListener.onClick(dialogFinal, 0, isChecked);
//                if (isChecked) {
//                    button.setEnabled(true);
//                } else {
//                    button.setEnabled(false);
//                }
//            }
//        });
//        //设置背景透明,去四个角
//        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
//        dialog.show();
//        dialog.getWindow().setLayout(dpChangePx(context, 290), LinearLayout.LayoutParams.WRAP_CONTENT);
//        dialog.setContentView(view);
//
//        return dialog;
//    }
}
