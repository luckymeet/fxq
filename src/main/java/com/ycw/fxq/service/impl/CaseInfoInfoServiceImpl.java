package com.ycw.fxq.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ycw.fxq.bean.CaseInfo;
import com.ycw.fxq.bean.CaseInfoVo;
import com.ycw.fxq.bean.request.CaseInfoRequest;
import com.ycw.fxq.common.page.PageParams;
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
	public List<CaseInfoVo> queryAll(CaseInfoRequest request, PageParams pageParams) {
		Map<String,String> searchMap = new HashMap<>();
		searchMap.put("caseName",request.getCaseName());
		searchMap.put("caseCharger",request.getCaseCharger());
		return caseInfoMapper.findAll(searchMap);
	}
}
