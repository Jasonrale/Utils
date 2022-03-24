package com.jd.mlaas.ump.api.domain.common.util;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * java验证各种格式类型工具类
 * cheng jiangyu
 * 2012-9-3 上午09:15:57
 */
public class RegValidateUtil {

    //报警类别
    public static String alarmWay = "0,1,2,3,4,5,6";

    /**
     * 验证邮箱
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        boolean flag = false;
        try {
            String check = "^[a-z]([a-z0-9]*[-_]?[a-z0-9]+)*@([a-z0-9]*[-_]?[a-z0-9]+)+[\\.][a-z]{2,3}([\\.][a-z]{2})?$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 验证是字符是否符合要求
     *
     * @param key
     * @return
     */
    public static boolean checkAccessKey(String key) {
        String regEx = "[a-zA-Z0-9][a-zA-Z0-9_.-]*$";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(key);
        return m.find();
    }

    /**
     * 验证手机号码
     *
     * @param mobileNumber
     * @return
     */
    public static boolean isMobileNumber(String mobileNumber) {
        boolean flag = false;
        try {
            String regEx = "^(((13[0-9])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$";
            Pattern pattern = Pattern.compile(regEx);
            Matcher matcher = pattern.matcher(mobileNumber);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    //验证中文    只能包含字母、数字、下划线、中划线或汉字
    public static boolean isChinese(String input) {
        if ((input).matches("[-a-zA-Z0-9_\u4e00-\u9fa5]*")) {
            return true;
        } else {
            return false;
        }
    }

    //验证英文   必须以英文字母开头，且只能包含字母、数字、下划线或中划线
    public static boolean isEnglish(String input) {
        if ((input).matches("^[a-zA-Z0-9][-a-zA-Z0-9_.]*$")) {
            return true;
        } else {
            return false;
        }
    }

    //是否包含特殊字符    不包含返回true，包含返回false
    public static boolean isNotCotainSpecialChar(String input) {
        if (input.matches("[^<>?\\$#]+")) {
            return true;
        } else {
            return false;
        }
    }

    //验证输入是否是数字
    public static boolean isNumeric(String input) {
        if (input.matches("[1-9][0-9]*")) {
            return true;
        } else {
            return false;
        }
    }


    //验证输入是否是数字  可以包括0
    public static boolean isNumericContainsZero(String input) {
        if (input.matches("[0-9]*")) {
            return true;
        } else {
            return false;
        }
    }

    //验证输入只能为 192.168.1.2:8080   这种形式
    public static boolean isIpPort(String input) {
        String[] ipAndPort = input.split("\\:");
        if (ipAndPort.length != 2) {
            return false;
        }
        if (!RegValidateUtil.isIp(ipAndPort[0])) {
            return false;
        } else if (!RegValidateUtil.isNumericContainsZero(ipAndPort[1])) {
            return false;
        } else if (Integer.parseInt(ipAndPort[1]) < 0 || Integer.parseInt(ipAndPort[1]) > 65535) {
            return false;
        }
        return true;
    }

    //验证输入只能为 192.168.1.2 这种形式
    public static boolean isIp(String input) {
        String[] ips = input.split("\\.");
        if (ips.length != 4) {
            return false;
        }
		/*if(Integer.parseInt(ips[0]) == 0 || Integer.parseInt(ips[3]) == 0 || Integer.parseInt(ips[3]) == 255) {
			return false;
		} */
        if (Integer.parseInt(ips[0]) == 0) {
            return false;
        }
        for (String ip : ips) {
            if (!RegValidateUtil.isNumericContainsZero(ip)) {
                return false;
            } else {
                if (Integer.parseInt(ip) < 0 || Integer.parseInt(ip) > 255) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 验证匹配是否正确
     *
     * @param paramStr 前台传入参数 如：1,2
     * @param standStr 标准数据 如：1,2,3,4,5
     */
    public static boolean isComparStr(String paramStr, String standStr) {
        if (paramStr == null || "".equals(paramStr) || standStr == null || "".equals(standStr)) {
            return false;
        }
        String[] alarmWay = standStr.split(",");
        String[] pAlarmWay = paramStr.split(",");
        for (int i = 0; i < pAlarmWay.length; i++) {
            boolean isEqual = false;
            String pValue = pAlarmWay[i];
            for (int k = 0; k < alarmWay.length; k++) {
                if (pValue.equals(alarmWay[k])) {
                    isEqual = true;
                    break;
                }
            }
            if (!isEqual) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkLength(int maxLength, String str) {
        return str.getBytes().length < maxLength;
    }

    public static boolean isInRange(int current, int min, int max) {
        return Math.max(min, current) == Math.min(current, max);
    }

}
