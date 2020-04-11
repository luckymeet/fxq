package com.ycw.fxq.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ycw.fxq.bean.CaseInfoVo;
import com.ycw.fxq.bean.request.CaseInfoRequest;
import com.ycw.fxq.common.page.PageInfo;
import com.ycw.fxq.common.page.PageParams;
import com.ycw.fxq.common.response.ResponseVO;
import com.ycw.fxq.service.CaseInfoService;

/**
 * 案件Controller
 *
 */
@RestController
@RequestMapping("/case")
public class CaseInfoController {
    @Autowired
    private CaseInfoService caseService;
    /**
     *  请求列表 find?caseName=XXX&caseCharger=XXX
     * @return
     */
    @GetMapping("/find")
    public ResponseVO<PageInfo<CaseInfoVo>> find(CaseInfoRequest request, PageParams pageParams) {
        List<CaseInfoVo> list = caseService.queryAll(request,pageParams);
		return ResponseVO.success(new PageInfo<>(list));
    }
}
