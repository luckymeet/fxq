package com.ycw.fxq.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ycw.fxq.bean.CaseInfoVO;
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
     * 列表 请求参数+分页参数
     * @param request
     * @param pageParams
     * @return
     */
    @GetMapping("/find")
    public ResponseVO<PageInfo<CaseInfoVO>> find(CaseInfoRequest request, PageParams pageParams) {
        List<CaseInfoVO> list = caseService.queryAll(request,pageParams);
		return ResponseVO.success(new PageInfo<>(list));
    }

    /**
     * 以下为新增/修改操作
     * 新增/修改 传入CaseInfoVo
     * 删除传入 id
     */
    /**
     * 前端传参参考login2.html中$('#btn_login').click(function(){代码
     */
}
