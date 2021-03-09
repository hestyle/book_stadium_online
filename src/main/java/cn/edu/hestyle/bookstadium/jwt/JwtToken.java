package cn.edu.hestyle.bookstadium.jwt;

import cn.edu.hestyle.bookstadium.entity.User;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * JwtToken 注解
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/9 11:30 上午
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JwtToken {
    /** 默认需要token */
    boolean required() default true;
    /** 允许的角色 */
    String[] authorizedRoles() default User.USER_ROLE;
}
