package com.ycw.fxq.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ycw.fxq.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User实体类
 */
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("user_info")
public class UserInfo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private String roleId;
    private String roleName;
}
