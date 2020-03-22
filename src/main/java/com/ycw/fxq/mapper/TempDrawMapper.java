package com.ycw.fxq.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.ycw.fxq.bean.Node;
import com.ycw.fxq.bean.TempDraw;
import com.ycw.fxq.common.base.BaseCrudMapper;

@Mapper
public interface TempDrawMapper extends BaseCrudMapper<TempDraw> {

	List<TempDraw> findall(Map<String, Integer> params);

	List<Node> findname();

	List<TempDraw> filterData(Map<String, String> params);

	Integer getTotalCount();
}