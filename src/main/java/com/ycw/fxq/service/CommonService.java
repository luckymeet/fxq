 package com.ycw.fxq.service;

import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.ycw.fxq.bean.Node;
import com.ycw.fxq.bean.TempDraw;
import com.ycw.fxq.bean.TempDrawVO;

/**
 * 公共业务逻辑Service
 * @author ycw
 * @date 2020/03/26 14:04:41
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
public interface CommonService {

	/**
	 * 获取聚类后的节点列表
	 *
	 * @author ycw
	 * @date 2020/03/26 13:49:45
	 * @param linkList 边列表
	 * @param nameList 节点列表
	 * @return
	 */
	List<Node> getClusterNodeList(List<TempDrawVO> linkList, List<String> nameList);

	/**
	 * 查找有向图节点间路径
	 *
	 * @author ycw
	 * @date 2020/03/26 13:49:45
	 * @param res      结果集
	 * @param previous 当前路径
	 * @param cur      当前节点
	 * @param des      目标节点
	 */
	void findLoops(Map<String, String> data, List<List<String>> res, Stack<String> previous, String cur, String des);

	/**
	 * 根据流水卡号组装有向图模型（利用Map表示有向图），key:节点，value:相邻节点（多个以英文逗号隔开）
	 *
	 * @author ycw
	 * @date 2020/03/26 14:00:19
	 * @param linkList 流水记录列表
	 * @return
	 */
	Map<String, String> createDirectedGraphByAccNo(List<TempDrawVO> linkList);

	/**
	 * 根据流水账户名组装有向图模型（利用Map表示有向图），key:节点，value:相邻节点（多个以英文逗号隔开）
	 *
	 * @author ycw
	 * @date 2020/03/26 14:00:19
	 * @param drawList 流水记录列表
	 * @return
	 */
	Map<String, String> createDirectedGraphByAccName(List<TempDraw> drawList);

}
