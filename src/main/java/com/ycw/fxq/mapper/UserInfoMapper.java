package com.ycw.fxq.mapper;

import com.ycw.fxq.bean.CaseInfo;
import com.ycw.fxq.bean.CaseInfoVO;
import com.ycw.fxq.bean.UserInfo;
import com.ycw.fxq.common.base.BaseCrudMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserInfoMapper extends BaseCrudMapper<UserInfo> {
}
