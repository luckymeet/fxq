package com.ycw.fxq.controller;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ycw.fxq.bean.TempDraw;
import com.ycw.fxq.common.response.ResponseVO;
import com.ycw.fxq.service.impl.CommonService;
import com.ycw.fxq.service.impl.TempDrawService;

/**
 * 交易流水
 * @author ycw
 * @date 2020/03/24 16:43:47
 * @version 1.00
 *
 * @record
 * <pre>
 * version  author      date          desc
 * -------------------------------------------------
 * 1.00     ycw         2020/03/24    新建
 * -------------------------------------------------
 * </pre>
 */
@RestController
public class TransferRecordController {

	@Autowired
	private TempDrawService tempDrawService;

	@Autowired
	private CommonService commonService;

	private static final String LOOP_QUERY = "1";

	/**
	 * 查找有向图间的路径
	 *
	 * @author ycw
	 * @date 2020/03/24 16:38:37
	 * @param startTime   开始时间
	 * @param endTime     结束时间
	 * @param cardNos     账号（多个账号以逗号隔开）
	 * @param payAcntName 付款账号
	 * @param recAcntName 收款账号
	 * @param type        类型：1-查询环路，2-查询路径
	 * @return
	 */
	@GetMapping("/draw/path/list")
	public ResponseVO<List<Map<String, String>>> findPath(String startTime, String endTime, String cardNos,
			String payAcntName, String recAcntName, String type) {
		List<List<String>> pathList = null;
		if (LOOP_QUERY.equals(type)) {
			// 获取账户间的环路
			pathList = findLoop(startTime, endTime, cardNos);
		} else {
			// 获取两个账户间的所有路径
			pathList = findPath(startTime, endTime, payAcntName, recAcntName);
		}

		/* 路线列表 1——2——3——1 */
		List<Map<String, String>> loopResult = pathList.stream().map(stringList -> {
			Map<String, String> map = new HashMap<>(5);
			map.put("startNode", stringList.get(0));
			map.put("endNode", stringList.get(stringList.size() - 1));
			map.put("path", stringList.stream().collect(Collectors.joining("——")));
			return map;
		}).collect(Collectors.toList());

		return ResponseVO.success(loopResult);
	}

	private List<List<String>> findPath(String startTime, String endTime, String payAcntName, String recAcntName) {
		payAcntName = new String(payAcntName.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
		recAcntName = new String(recAcntName.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
		// 根据时间范围查询流水
		List<TempDraw> drawList = tempDrawService.findTempDrawList(startTime, endTime);
		// 组装有向图模型（利用Map表示有向图）
		Map<String, String> dataMap = commonService.createDirectedGraphByMap(drawList);

		/* 调用算法求路径 */
		List<List<String>> pathList = new ArrayList<>();
		if (StringUtils.isNotBlank(payAcntName) && StringUtils.isNotBlank(recAcntName)) {
			Stack<String> previous = new Stack<>();
			previous.push(payAcntName);
			commonService.findLoops(dataMap, pathList, previous, payAcntName, recAcntName);
		}
		return pathList;
	}

	private List<List<String>> findLoop(String startTime, String endTime, String cardNos) {
		// 根据时间范围查询流水
		List<TempDraw> drawList = tempDrawService.findTempDrawList(startTime, endTime);
		// 组装有向图模型（利用Map表示有向图）
		Map<String, String> dataMap = commonService.createDirectedGraphByMap(drawList);

		/* 调用算法求环路 */
		String[] cardNoArray = StringUtils.split(StringUtils.trimToEmpty(cardNos), ',');
		List<List<String>> loopList = new ArrayList<>();
		for (int i = 0; i < cardNoArray.length; i++) {
			String cardNo = cardNoArray[i];
			Stack<String> previous = new Stack<>();
			previous.push(cardNo);
			commonService.findLoops(dataMap, loopList, previous, cardNo, cardNo);
		}
		return loopList;
	}

}
