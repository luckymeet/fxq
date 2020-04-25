package com.ycw.fxq.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ycw.fxq.bean.CaseInfo;
import com.ycw.fxq.bean.CaseInfoVO;
import com.ycw.fxq.bean.request.CaseInfoRequest;
import com.ycw.fxq.common.page.PageParams;
import com.ycw.fxq.common.utils.UserInfoUtils;
import com.ycw.fxq.mapper.CaseInfoMapper;
import com.ycw.fxq.service.CaseInfoService;
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
	 * 删除
	 * @param id
	 */
	@Override
	public void delete(Integer id) {
		caseInfoMapper.deleteById(id);
	}

	/**
	 * 以下为新增/修改，根据有无id做删除/新增
	 */
	public void update(CaseInfoVO caseInfoVO) {
		// caseInfoVo翻入caseInfo,调用各个set方法
		CaseInfo caseInfo = new CaseInfo();
		BeanUtils.copyProperties(caseInfoVO, caseInfo);

		if (caseInfoVO.getId() != null) {
			// 不为空
			caseInfoMapper.updateById(caseInfo);
			return;
		}
		// if ID为空
		caseInfoMapper.insert(caseInfo);
	}

	@Override
	public CaseInfoVO query(Integer id) {
		CaseInfo caseInfo = caseInfoMapper.selectById(id);
		CaseInfoVO caseInfoVO = new CaseInfoVO();
		BeanUtils.copyProperties(caseInfo, caseInfoVO);
		return caseInfoVO;
	}
}
