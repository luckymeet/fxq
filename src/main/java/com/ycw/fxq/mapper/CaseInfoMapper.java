package com.ycw.fxq.mapper;

import com.ycw.fxq.bean.CaseInfo;
import com.ycw.fxq.bean.CaseInfoVO;
import com.ycw.fxq.common.base.BaseCrudMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface CaseInfoMapper extends BaseCrudMapper<CaseInfo> {
    /**
     * 查找
     * @param searchMap
     * @return
     */
    List<CaseInfoVO> findAll(Map<String,String> searchMap);
}
