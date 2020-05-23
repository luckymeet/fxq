package com.ycw.fxq.shiro.realm;

import com.ycw.fxq.bean.UserInfo;
import com.ycw.fxq.service.UserInfoService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2018/6/24 0024.
 */
public class UserRealm extends AuthorizingRealm {
    @Autowired
    private UserInfoService userInfoService;

    // 授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String username = (String) principalCollection.getPrimaryPrincipal();

        // 从数据库中根据用户名获取角色数据
        Set<String> roles = getRolesByUsername(username);
        // 从数据库中根据用户名获取权限数据
        Set<String> permissions = getPermissionbyUsername(username);

        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.setStringPermissions(permissions);
        simpleAuthorizationInfo.setRoles(roles);
        return simpleAuthorizationInfo;
    }

    /**
     * 模拟用户权限信息
     * @param username
     * @return
     */
    private Set<String> getPermissionbyUsername(String username) {
        Set<String> sets = new HashSet<>();
        sets.add("user:delete");
        sets.add("user:add");
        return sets;
    }

    /**
     * 模拟根据用户名获取数据库中的角色数据
     * @param username
     * @return
     */
    private Set<String> getRolesByUsername(String username) {
        Set<String> sets = new HashSet<>();
        UserInfo userInfo = userInfoService.get(username);
        sets.add(userInfo.getRoleId());
        return sets;
    }

    // 认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        // 1.从主体传过来的认证信息中，获取用户名
        String username = (String) authenticationToken.getPrincipal();

        // 2.通过用户名去到数据库中获取凭证
        String password = getPasswordByUsername(username);
        if(password == null) {
            return null;
        }
        return new SimpleAuthenticationInfo(username, password, "userRealm");
    }

    /**
     * 模拟数据库查询凭证
     * @param username
     * @return
     */
    private String getPasswordByUsername(String username) {
        UserInfo userInfo = userInfoService.get(username);
        if(userInfo == null) {
            return null;
        }
        return userInfo.getPassword();
    }
}