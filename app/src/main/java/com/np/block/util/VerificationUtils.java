package com.np.block.util;

import android.text.TextUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.regex.Pattern.compile;

/**
 * 验证工具类
 * @author fengxin
 */
public class VerificationUtils {

    /**
     * 手机位数
     */
    private static final int PHONE_NUM = 11;

    /** 手机号规则  */
    private static final String PHONE_PATTERN = "^((13[0-9])|(14[0-9])|(15[0-9])|(16[0-9])|(17[0-9])|(18[0-9])|(19[0-9]))\\d{8}$";

    /** 密码规则（6-16位字母、数字） */
    private static final String PASSWORD_PATTERN="^[0-9A-Za-z]{6,16}$";

    /**
     * 手机号校验
     * @param phoneNum 手机号
     * @return boolean
     */
    public static boolean validatePhone(String phoneNum){
        if(phoneNum.length() != PHONE_NUM) {
            return false;
        }
        String kong = " ";
        if(phoneNum.contains(kong)) {
            return false;
        }
        Pattern p = compile(PHONE_PATTERN);
        Matcher m = p.matcher(phoneNum);
        return m.find();
    }

    /**
     * 验证密码格式  6-16 位字母、数字
     * @param pwd 密码
     * @return boolean
     */
    public static boolean validatePwd(String pwd) {
        if (TextUtils.isEmpty(pwd)){
            return false;
        }
        return Pattern.matches(PASSWORD_PATTERN, pwd);
    }

    /**
     * 验证昵称
     * @param name 昵称
     * @return boolean
     */
    public static boolean validateName(String name) {
        if (TextUtils.isEmpty(name)){
            return false;
        }else return !name.contains(" ");
    }

    /**
     * 验证游戏名字 TODO 暂时不加限制
     *
     * @param gameName 游戏名字
     * @return boolean
     */
    public static boolean validateGameName(String gameName) {
        if (TextUtils.isEmpty(gameName)){
            return false;
        }else {
            return !gameName.contains(" ");
        }
    }

    private VerificationUtils() {}
}
