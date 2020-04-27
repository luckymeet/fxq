package com.ycw.fxq.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ycw.fxq.bean.Node;
import com.ycw.fxq.bean.TempDraw;
import com.ycw.fxq.bean.TempDrawVO;
import com.ycw.fxq.common.utils.BeanHandleUtils;
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
	public List<TempDrawVO> findTempDrawList(String startTime, String endTime) {
		LambdaQueryWrapper<TempDraw> queryWrapper = Wrappers.lambdaQuery();
		if (StringUtils.isNotBlank(startTime)) {
			queryWrapper.ge(TempDraw::getTime, startTime);
		}
		if (StringUtils.isNotBlank(endTime)) {
			queryWrapper.le(TempDraw::getTime, endTime);
		}
		List<TempDraw> drawList = tempDrawMapper.selectList(queryWrapper);
		return BeanHandleUtils.listCopy(drawList, TempDrawVO.class);
	}

	/**
	 * 根据账户名查询所有账号
	 * @author yuminjun
	 * @date 2020/04/27 14:51:37
	 * @param acntNameList
	 * @return
	 */
	@Override
 	public Set<String> findAcntNoListByAcntNameList(List<String> acntNameList) {
		LambdaQueryWrapper<TempDraw> queryWrapper = Wrappers.lambdaQuery();
		queryWrapper.in(TempDraw::getName1, acntNameList);
		List<TempDraw> drawList1 = tempDrawMapper.selectList(queryWrapper);

		queryWrapper = Wrappers.lambdaQuery();
		queryWrapper.in(TempDraw::getName2, acntNameList);
		List<TempDraw> drawList2 = tempDrawMapper.selectList(queryWrapper);

		Set<String> drawList = drawList1.stream().map(TempDraw::getCard1).collect(Collectors.toSet());
		drawList.addAll(drawList2.stream().map(TempDraw::getCard2).collect(Collectors.toSet()));
		return drawList;
	}

	/**
	 * 查询流水数据
	 * @author yuminjun
	 * @date 2020/04/25 16:17:44
	 * @param caseId 案件id
	 * @param acntNo 账号
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @return
	 */
	@Override
	public List<TempDraw> findDataByParams(String caseId, String acntNo, LocalDateTime startTime, LocalDateTime endTime) {
		LambdaQueryWrapper<TempDraw> queryWrapper = Wrappers.lambdaQuery();
		queryWrapper.eq(TempDraw::getCaseId, caseId);
		queryWrapper.eq(TempDraw::getCard1, acntNo);
		queryWrapper.ge(TempDraw::getTime, startTime);
		queryWrapper.le(TempDraw::getTime, endTime);
		List<TempDraw> drawList = this.tempDrawMapper.selectList(queryWrapper);
		return drawList;
	}

}
