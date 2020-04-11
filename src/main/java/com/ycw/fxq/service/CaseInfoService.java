package com.ycw.fxq.service;

import com.ycw.fxq.bean.CaseInfoVo;
import com.ycw.fxq.bean.request.CaseInfoRequest;
import com.ycw.fxq.common.page.PageParams;

import java.util.List;

public interface CaseInfoService {
    List<CaseInfoVo> queryAll(CaseInfoRequest request, PageParams pageParams);
}
