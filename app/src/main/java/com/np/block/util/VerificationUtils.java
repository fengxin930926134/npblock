package com.np.block.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.regex.Pattern.compile;

public class VerificationUtils {

    /**
     * 手机位数
     */
    private static final int PHONE_NUM = 11;

    /**
     * 手机号校验
     */
    public static boolean phoneValidate(String phoneNum){
        if(phoneNum.length() != PHONE_NUM) {
            return false;
        }
        String kong = " ";
        if(phoneNum.contains(kong)) {
            return false;
        }
        String regex = "^((13[0-9])|(14[0-9])|(15[0-9])|(16[0-9])|(17[0-9])|(18[0-9])|(19[0-9]))\\d{8}$";
        Pattern p = compile(regex);
        Matcher m = p.matcher(phoneNum);
        return m.find();
    }

    private VerificationUtils() {}
}