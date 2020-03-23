package com.ycw.fxq.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.regex.Pattern;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ycw.fxq.bean.Node;
import com.ycw.fxq.bean.TempDraw;
import com.ycw.fxq.mapper.TempDrawMapper;
import com.ycw.fxq.service.impl.TempDrawService;
import com.ycw.fxq.task.FindDataTask;

@Service
public class TempDrawServiceImpl extends ServiceImpl<TempDrawMapper, TempDraw> implements TempDrawService {

	@Autowired
	TempDrawMapper dao;

	@Override
	public List<TempDraw> findDataByParams(Map<String, Integer> params) {
		return dao.findall(params);
	}

	@Override
	public List<Node> findname() {
		return dao.findname();
	}

	@Override
	public List<TempDraw> filterData(Map<String, String> params) {
		return dao.filterData(params);
	}

	@Override
	public Integer getTotalCount() {
		return dao.getTotalCount();
	}

	/**
	 * 并行查询数据库记录
	 *
	 * @return
	 */
	@Override
	public List<TempDraw> findAllData() {
		ForkJoinPool forkJoinPool = new ForkJoinPool();
		Integer count = this.getTotalCount();// 查询总数
		FindDataTask task = new FindDataTask(0, count);
		task.setService(this);
		return forkJoinPool.invoke(task);
	}

	@Override
	public List<TempDraw> findDataByList(String startTime, String endTime, String[] cardNo) {
		LambdaQueryWrapper<TempDraw> wrapper = Wrappers.lambdaQuery();
		if(StringUtils.isNotBlank(startTime)) {
			wrapper.gt(TempDraw::getTime,startTime);
		}
		if(StringUtils.isNotBlank(endTime)) {
			wrapper.lt(TempDraw::getTime,endTime);
		}
		if (cardNo != null && cardNo.length > 0) {
			wrapper.in(TempDraw::getCard1, cardNo);
		}
		return dao.selectList(wrapper);
	}
}
