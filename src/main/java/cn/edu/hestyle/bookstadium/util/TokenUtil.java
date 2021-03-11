package cn.edu.hestyle.bookstadium.util;

import cn.edu.hestyle.bookstadium.entity.StadiumManager;
import cn.edu.hestyle.bookstadium.entity.SystemManager;
import cn.edu.hestyle.bookstadium.entity.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/9 3:39 下午
 */
public class TokenUtil {
    /** token 有效时长1个月（单位是毫秒） */
    public static final long TOKEN_EXPIRES = 1000L * 60 * 60 * 24 * 30;

    private static final Logger logger = LoggerFactory.getLogger(TokenUtil.class);

    /**
     * 生成user token
     * @param user              user
     * @return                  token
     */
    public static String getToken(User user) {
        String token = "";
        token = JWT.create().withAudience(user.getId() + "", User.USER_ROLE)
                .withExpiresAt(new Date(System.currentTimeMillis() + TOKEN_EXPIRES))
                .sign(Algorithm.HMAC256(user.getPassword()));
        logger.info("User userId = " + user.getId() + " 成功生成 Token = " + token);
        return token;
    }

    /**
     * 生成stadiumManager token
     * @param stadiumManager    stadiumManager
     * @return                  token
     */
    public static String getToken(StadiumManager stadiumManager) {
        String token = "";
        token = JWT.create().withAudience(stadiumManager.getId() + "", StadiumManager.STADIUM_MANAGER_ROLE)
                .withExpiresAt(new Date(System.currentTimeMillis() + TOKEN_EXPIRES))
                .sign(Algorithm.HMAC256(stadiumManager.getPassword()));
        logger.info("StadiumManager stadiumManagerId = " + stadiumManager.getId() + " 成功生成 Token = " + token);
        return token;
    }

    /**
     * 生成systemManager toke
     * @param systemManager     systemManager
     * @return                  token
     */
    public static String getToken(SystemManager systemManager) {
        String token = "";
        token = JWT.create().withAudience(systemManager.getId() + "", SystemManager.SYSTEM_MANAGER_ROLE)
                .withExpiresAt(new Date(System.currentTimeMillis() + TOKEN_EXPIRES))
                .sign(Algorithm.HMAC256(systemManager.getPassword()));
        logger.info("SystemManager systemManagerId = " + systemManager.getId() + " 成功生成 Token = " + token);
        return token;
    }
}
