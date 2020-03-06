package com.ycw.fxq.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.ycw.fxq.bean.Node;
import com.ycw.fxq.bean.TempDraw;

@Mapper
public interface TempDrawDao {

	String creatstrid();

	ArrayList<TempDraw> findall(Map<String, Integer> params);

	ArrayList<Node> findname();

	ArrayList<TempDraw> findallreal();

	ArrayList<String> findnamereal();

	List<TempDraw> filterData(Map<String, String> params);

	Integer getTotalCount();
}
