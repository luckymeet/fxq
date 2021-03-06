package com.ycw.fxq.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ycw.fxq.bean.Node;
import com.ycw.fxq.bean.TempDraw;
import com.ycw.fxq.bean.TempDrawVO;

public interface TempDrawService extends IService<TempDraw> {

	/**
	 * 根据条件查询所有流水
	 * @author ycw
	 * @date 2020/03/26 14:12:08
	 * @param params 条件参数
	 * @return
	 */
	List<TempDrawVO> findDataByParams(Map<String, Object> params);

	/**
	 * 查询所有流水的付款账户名和收款账户名（已去重）
	 * @author ycw
	 * @date 2020/03/26 14:12:41
	 * @return
	 */
	List<Node> findname();

	/**
	 * 根据条件过滤流水
	 * @author ycw
	 * @date 2020/03/26 15:15:39
	 * @param params 条件参数
	 * @return
	 */
	List<TempDrawVO> filterData(Map<String, String> params);

	/**
	 * 获取数据库流水记录总数
	 * @author ycw
	 * @date 2020/03/26 15:16:03
	 * @return
	 */
	Integer getTotalCount();

	/**
	 * 并行查询所有流水
	 * @author ycw
	 * @date 2020/03/26 15:16:19
	 * @return
	 */
	List<TempDrawVO> findAllData();

	/**
	 * 根据时间范围查询流水
	 *
	 * @author ycw
	 * @date 2020/03/24 16:50:03
	 * @param startTime 开始时间
	 * @param endTime   结束时间
	 * @return
	 */
	List<TempDrawVO> findTempDrawList(LocalDateTime startTime, LocalDateTime endTime);

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
	List<TempDraw> findDataByParams(String caseId, String acntNo, LocalDateTime startTime, LocalDateTime endTime);

	/**
	 * 根据账户名查询所有账号
	 * @author yuminjun
	 * @date 2020/04/27 14:51:37
	 * @param acntNameList
	 * @return
	 */
	List<String> findAcntNoListByAcntNameList(List<String> acntNameList);

}
