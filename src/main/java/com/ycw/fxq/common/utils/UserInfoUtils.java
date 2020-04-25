package com.ycw.fxq.common.utils;

import com.ycw.fxq.bean.User;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class UserInfoUtils {
    public static User getUserInfo() {
        // getSession().getAttribute("user") TODO
        User user = new User();
        user.setId(1L);

        return user;
    }

    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

    }

    public static HttpSession getSession() {
        return getRequest().getSession();
    }
}
