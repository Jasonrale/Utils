package com.jd.mlaas.ump.api.domain.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegexUtils {

    private RegexUtils(){}

    /**
     * 判断是否为IP
     *
     * @param addr
     * @return
     */
    public static boolean isIpv4(String addr) {
        if (addr.length() < 7 || addr.length() > 15 || "".equals(addr)) {
            return false;
        }
        String rexp = "^(25[0-5]|2[0-4]\\d|1[0-9][0-9]|1[0-9]|[1-9]|[1-9][0-9])(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){2}(\\.(25[0-5]|2[0-4]\\d|1[0-9][0-9]|1[0-9]|[0-9]|[1-9][0-9]))$";
        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(addr);
        return mat.find();
    }

}
