package com.ycw.fxq.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.ycw.fxq.bean.Node;
import com.ycw.fxq.bean.TempDraw;
import com.ycw.fxq.bean.TempDrawVO;
import com.ycw.fxq.common.base.BaseCrudMapper;

@Mapper
public interface TempDrawMapper extends BaseCrudMapper<TempDraw> {

	List<TempDrawVO> findall(Map<String, Object> params);

	List<Node> findname();

	List<TempDrawVO> filterData(Map<String, String> params);

	Integer getTotalCount();

	List<String> findAcntNoListByAcntNameList(List<String> acntNameList);
}
