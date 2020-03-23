package com.ycw.fxq.service.impl;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ycw.fxq.bean.Node;
import com.ycw.fxq.bean.TempDraw;

public interface TempDrawService extends IService<TempDraw> {

	List<TempDraw> findDataByParams(Map<String, Integer> params);

	List<Node> findname();

	List<TempDraw> filterData(Map<String, String> params);

	Integer getTotalCount();

	List<TempDraw> findAllData();

	List<TempDraw> findDataByList(String startTime, String endTime, String[] cardNo);

}
