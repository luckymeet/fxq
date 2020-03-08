package com.ycw.fxq.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.ycw.fxq.bean.Node;
import com.ycw.fxq.bean.TempDraw;

@Mapper
public interface TempDrawDao {

	String creatstrid();

	List<TempDraw> findall(Map<String, Integer> params);

	List<Node> findname();

	List<TempDraw> findallreal();

	List<String> findnamereal();

	List<TempDraw> filterData(Map<String, String> params);

	Integer getTotalCount();
}
