package cn.edu.hestyle.bookstadium.util;

import org.springframework.util.DigestUtils;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/9 4:56 下午
 */
public class EncryptUtil {
    /**
     * 对原始密码和盐值执行MD5加密
     * @param srcPassword 原始密码
     * @param saltValue 盐值
     * @return 加密后的密码
     */
    public static String encryptPassword(String srcPassword, String saltValue) {
        String src = saltValue + srcPassword + saltValue;
        for (int i = 0; i < 10 ; i++) {
            src = DigestUtils.md5DigestAsHex(src.getBytes()).toUpperCase();
        }
        return src;
    }
}
