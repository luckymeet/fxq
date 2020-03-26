package com.ycw.fxq.service.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ycw.fxq.bean.Node;
import com.ycw.fxq.bean.TempDraw;
import com.ycw.fxq.bean.TempDrawVO;
import com.ycw.fxq.mapper.TempDrawMapper;
import com.ycw.fxq.service.TempDrawService;
import com.ycw.fxq.task.FindDataTask;

/**
 * 流水记录Service实现类
 * @author ycw
 * @date 2020/03/26 14:03:32
 * @version 1.00
 *
 * @record
 * <pre>
 * version  author      date          desc
 * -------------------------------------------------
 * 1.00     ycw         2020/03/26    新建
 * -------------------------------------------------
 * </pre>
 */
@Service
public class TempDrawServiceImpl extends ServiceImpl<TempDrawMapper, TempDraw> implements TempDrawService {

	@Autowired
	TempDrawMapper tempDrawMapper;

	@Override
	public List<TempDrawVO> findDataByParams(Map<String, Integer> params) {
		return tempDrawMapper.findall(params);
	}

	@Override
	public List<Node> findname() {
		return tempDrawMapper.findname();
	}

	@Override
	public List<TempDrawVO> filterData(Map<String, String> params) {
		return tempDrawMapper.filterData(params);
	}

	@Override
	public Integer getTotalCount() {
		return tempDrawMapper.getTotalCount();
	}

	/**
	 * 并行查询数据库流水记录
	 *
	 * @author yuminjun
	 * @date 2020/03/26 14:03:16
	 * @return
	 */
	@Override
	public List<TempDrawVO> findAllData() {
		ForkJoinPool forkJoinPool = new ForkJoinPool();
		Integer count = this.getTotalCount();// 查询总数
		FindDataTask task = new FindDataTask(0, count);
		task.setService(this);
		return forkJoinPool.invoke(task);
	}

	/**
	 * 根据时间范围查询流水
	 *
	 * @author ycw
	 * @date 2020/03/24 16:50:03
	 * @param startTime 开始时间
	 * @param endTime   结束时间
	 * @return
	 */
	@Override
	public List<TempDraw> findTempDrawList(String startTime, String endTime) {
		LambdaQueryWrapper<TempDraw> queryWrapper = Wrappers.lambdaQuery();
		if (StringUtils.isNotBlank(startTime)) {
			queryWrapper.ge(TempDraw::getTime, startTime);
		}
		if (StringUtils.isNotBlank(endTime)) {
			queryWrapper.le(TempDraw::getTime, endTime);
		}
		List<TempDraw> drawList = tempDrawMapper.selectList(queryWrapper);
		return drawList;
	}

}
