package com.ycw.fxq.controller;

import com.ycw.fxq.bean.request.LoginRequest;
import com.ycw.fxq.common.response.ResponseVO;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController
@RequestMapping("/login")
public class LoginController {
    @PostMapping
    public ResponseVO<String> login(@RequestBody LoginRequest loginRequest){
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(loginRequest.getUsername(), loginRequest.getPassword());
        try {
            subject.login(token);
        } catch (AuthenticationException e) {
            return ResponseVO.fail(1001,"登录失败");
        }
        return ResponseVO.success("登录成功");
    }
}
