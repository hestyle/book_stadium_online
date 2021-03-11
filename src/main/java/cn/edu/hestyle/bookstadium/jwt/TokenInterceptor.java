package cn.edu.hestyle.bookstadium.jwt;

import cn.edu.hestyle.bookstadium.entity.StadiumManager;
import cn.edu.hestyle.bookstadium.entity.SystemManager;
import cn.edu.hestyle.bookstadium.entity.User;
import cn.edu.hestyle.bookstadium.jwt.exception.TokenVerificationFailedException;
import cn.edu.hestyle.bookstadium.service.IStadiumManagerService;
import cn.edu.hestyle.bookstadium.service.ISystemManagerService;
import cn.edu.hestyle.bookstadium.service.IUserService;
import com.auth0.jwt.JWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/9 11:41 上午
 */
public class TokenInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(TokenInterceptor.class);

    @Autowired
    private IUserService userService;
    @Autowired
    private IStadiumManagerService stadiumManagerService;
    @Autowired
    private ISystemManagerService systemManagerService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 判断是否为需要拦截的方法
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod)handler;
        Method method = handlerMethod.getMethod();
        // 检查是否有JwtToken注解
        if (!method.isAnnotationPresent(JwtToken.class)) {
            return true;
        }
        JwtToken jwtToken = method.getAnnotation(JwtToken.class);
        // 判断是否需要验证token
        if (!jwtToken.required()) {
            return true;
        }
        String token = request.getHeader("token");
        // 读取token中的账号id、角色
        Integer accountId;
        String accountRole;
        try {
            accountId = Integer.parseInt(JWT.decode(token).getAudience().get(0));
            accountRole = JWT.decode(token).getAudience().get(1);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Token解析失败！token = " + token);
            throw new TokenVerificationFailedException("Token无效，请重新登录！");
        }
        // 检查token是否过期
        Date expiresDate = JWT.decode(token).getExpiresAt();
        if (expiresDate.before(new Date())) {
            logger.warn("Token 已过期！token = " + token);
            throw new TokenVerificationFailedException("Token 已失效，请重新登录！");
        }
        // 判断token角色
        String[] authorizedRoles = jwtToken.authorizedRoles();
        boolean isAuthorizedRole = false;
        for (int i = 0; i < authorizedRoles.length; ++i) {
            if (authorizedRoles[i].equals(accountRole)) {
                isAuthorizedRole = true;
                break;
            }
        }
        if (!isAuthorizedRole) {
            logger.warn("Token 该token = " + token + "无权访问该接口！url = " + request.getRequestURI());
            throw new TokenVerificationFailedException("Token无法访问该接口！");
        }
        // 验证token是否与数据库一致
        if (User.USER_ROLE.equals(accountRole)) {
            // User角色的token验证
            User user = null;
            try {
                user = userService.systemFindById(accountId);
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("Token验证失败！token = " + token);
                throw new TokenVerificationFailedException("Token无效，请重新登录！");
            }
            if (user == null || !token.equals(user.getToken())) {
                logger.warn("Token验证失败！token = " + token);
                throw new TokenVerificationFailedException("Token无效，请重新登录！");
            }
            logger.warn("Token通过验证！token = " + token + "，user = " + user);
        } else if (StadiumManager.STADIUM_MANAGER_ROLE.equals(accountRole)) {
            // StadiumManager角色的token验证
            StadiumManager stadiumManager = null;
            try {
                stadiumManager = stadiumManagerService.systemFindById(accountId);
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("Token验证失败！token = " + token);
                throw new TokenVerificationFailedException("Token无效，请重新登录！");
            }
            if (stadiumManager == null || !token.equals(stadiumManager.getToken())) {
                logger.warn("Token验证失败！token = " + token);
                throw new TokenVerificationFailedException("Token无效，请重新登录！");
            }
            logger.warn("Token通过验证！token = " + token + "，stadiumManager = " + stadiumManager);
        } else if (SystemManager.SYSTEM_MANAGER_ROLE.equals(accountRole)) {
            // SystemManager角色的token验证
            SystemManager systemManager = null;
            try {
                systemManager = systemManagerService.systemFindById(accountId);
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("Token验证失败！token = " + token);
                throw new TokenVerificationFailedException("Token无效，请重新登录！");
            }
            if (systemManager == null || !token.equals(systemManager.getToken())) {
                logger.warn("Token验证失败！token = " + token);
                throw new TokenVerificationFailedException("Token无效，请重新登录！");
            }
            logger.warn("Token通过验证！token = " + token + "，systemManager = " + systemManager);
        } else {
            logger.warn("Token验证失败！token = " + token);
            throw new TokenVerificationFailedException("Token无效，请重新登录！");
        }
        // 然后将accountId、accountRole写入session
        request.getSession().setAttribute("id", accountId);
        request.getSession().setAttribute("role", accountRole);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
