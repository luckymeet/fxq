package com.ycw.fxq.controller;

import com.github.pagehelper.PageInfo;
import com.ycw.fxq.bean.CaseInfoVo;
import com.ycw.fxq.bean.request.CaseInfoRequest;
import com.ycw.fxq.common.page.PageParams;
import com.ycw.fxq.common.response.ResponseVO;
import com.ycw.fxq.service.CaseInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * 列表 请求参数+分页参数
     * @param request
     * @param pageParams
     * @return
     */
    @GetMapping("/find")
    public ResponseVO<List<CaseInfoVo>> find(CaseInfoRequest request, PageParams pageParams) {
        return ResponseVO.success(caseService.queryAll(request,pageParams));
    }
}
