package com.ycw.fxq.controller;

import java.util.List;

import com.ycw.fxq.bean.CaseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/legalCase")
public class CaseInfoController {
    @Autowired
    private CaseInfoService caseService;

    @GetMapping
    public ResponseVO<PageInfo<CaseInfoVO>> find(CaseInfoRequest request, PageParams pageParams) {
        List<CaseInfoVO> list = caseService.queryAll(request,pageParams);
		return ResponseVO.success(new PageInfo<>(list));
    }

    @GetMapping("/{id}")
    public ResponseVO<CaseInfoVO> findOne(@PathVariable Integer id) {
        return ResponseVO.success(caseService.query(id));
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseVO delete(@PathVariable Integer id) {
        caseService.delete(id);
        return ResponseVO.success(null);
    }

    @PostMapping()
    public ResponseVO saveOrUpdate(CaseInfoVO caseInfoVO) {
        caseService.update(caseInfoVO);
        return ResponseVO.success(null);
    }


    /**
     * 前端传参参考
     * login2.html
     * 中$('#btn_login').click(function(){代码
     */
}
