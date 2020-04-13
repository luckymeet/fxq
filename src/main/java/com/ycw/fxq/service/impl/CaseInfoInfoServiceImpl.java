package com.ycw.fxq.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ycw.fxq.bean.CaseInfo;
import com.ycw.fxq.bean.CaseInfoVO;
import com.ycw.fxq.bean.request.CaseInfoRequest;
import com.ycw.fxq.common.page.PageParams;
import com.ycw.fxq.common.utils.UserInfoUtils;
import com.ycw.fxq.mapper.CaseInfoMapper;
import com.ycw.fxq.service.CaseInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CaseInfo 的Service
 */
@Service
public class CaseInfoInfoServiceImpl extends ServiceImpl<CaseInfoMapper, CaseInfo> implements CaseInfoService {

	@Autowired
	private CaseInfoMapper caseInfoMapper;


	@Override
	public List<CaseInfoVO> queryAll(CaseInfoRequest request, PageParams pageParams) {
		Map<String,String> searchMap = new HashMap<>();
		searchMap.put("caseName",request.getCaseName());
		searchMap.put("caseCharger",request.getCaseCharger());
		searchMap.put("createUser", UserInfoUtils.getUserInfo().getId().toString());
		return caseInfoMapper.findAll(searchMap);
	}

	/**
	 * 以下为新增/修改，根据有无id做删除/新增
	 * 未添加到CaseInfoService，记得添加
	 */
	public void update(CaseInfoVO caseInfoVo) {
		// caseInfoVo翻入caseInfo,调用各个set方法
		CaseInfo caseInfo = new CaseInfo();
		caseInfo.setId((long)caseInfoVo.getId());// 如此往复

		// if ID为空
		caseInfoMapper.insert(caseInfo);

		// 不为空
		caseInfoMapper.updateById(caseInfo);
	}

	/**
	 * 以下为删除
	 */
	public void delete(int id) {
		caseInfoMapper.deleteById(id);
	}
}