package com.ycw.fxq.service;

import com.ycw.fxq.bean.CaseInfoVO;
import com.ycw.fxq.bean.request.CaseInfoRequest;
import com.ycw.fxq.common.page.PageParams;

import java.util.List;

public interface CaseInfoService {
    List<CaseInfoVO> queryAll(CaseInfoRequest request, PageParams pageParams);
    void delete(Integer id);
    void update(CaseInfoVO caseInfoVO);
    CaseInfoVO query(Integer id);
}
