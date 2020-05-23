package com.ycw.fxq.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ycw.fxq.bean.CaseInfo;
import com.ycw.fxq.bean.CaseInfoVO;
import com.ycw.fxq.bean.UserInfo;
import com.ycw.fxq.bean.request.CaseInfoRequest;
import com.ycw.fxq.common.page.PageParams;
import com.ycw.fxq.common.utils.UserInfoUtils;
import com.ycw.fxq.mapper.CaseInfoMapper;
import com.ycw.fxq.mapper.UserInfoMapper;
import com.ycw.fxq.service.CaseInfoService;
import com.ycw.fxq.service.UserInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CaseInfo 的Service
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

	@Autowired
	private UserInfoMapper userInfoMapper;

	@Override
	public UserInfo get(String username) {
		return userInfoMapper.selectOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUsername,username));
	}
}
