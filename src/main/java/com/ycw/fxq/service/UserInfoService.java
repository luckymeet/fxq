package com.ycw.fxq.service;

import com.ycw.fxq.bean.CaseInfoVO;
import com.ycw.fxq.bean.UserInfo;
import com.ycw.fxq.bean.request.CaseInfoRequest;
import com.ycw.fxq.common.page.PageParams;

import java.util.List;

public interface UserInfoService {
    public UserInfo get(String username);
}
