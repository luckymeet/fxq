package com.ycw.fxq.service.impl;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ycw.fxq.bean.Node;
import com.ycw.fxq.bean.TempDraw;
import com.ycw.fxq.bean.TempDrawVO;

public interface TempDrawService extends IService<TempDraw> {

	List<TempDrawVO> findDataByParams(Map<String, Integer> params);

	List<Node> findname();

	List<TempDrawVO> filterData(Map<String, String> params);

	Integer getTotalCount();

	List<TempDrawVO> findAllData();

	List<TempDraw> findTempDrawList(String startTime, String endTime);

}
