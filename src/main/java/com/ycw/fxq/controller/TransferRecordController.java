package com.ycw.fxq.controller;

import java.io.UnsupportedEncodingException;
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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ycw.fxq.bean.TempDraw;
import com.ycw.fxq.common.response.ResponseVO;
import com.ycw.fxq.service.impl.CommonService;
import com.ycw.fxq.service.impl.TempDrawService;

@RestController
public class TransferRecordController {
	@Autowired
	private TempDrawService tempDrawService;

	@Autowired
	private CommonService commonService;

	@GetMapping("/draw/path/list")
	public ResponseVO<List<Map<String, String>>> findPath(String startTime, String endTime, String cardNos,
			String payAcntName, String recAcntName, String type) {
		List<List<String>> pathList = null;
		if ("1".equals(type)) {
			pathList = findLoop(startTime, endTime, cardNos);
		} else {
			pathList = findPath(startTime, endTime, payAcntName, recAcntName);
		}

		/* 环路列表 1——2——3——1*/
		List<Map<String, String>> loopResult = pathList.stream().map(stringList -> {
			Map<String, String> map = new HashMap<>();
			map.put("startNode", stringList.get(0));
			map.put("endNode", stringList.get(stringList.size() - 1));
			map.put("path", stringList.stream().collect(Collectors.joining("——")));
			return map;
		}).collect(Collectors.toList());

		return ResponseVO.success(loopResult);
	}

	private List<List<String>> findPath(String startTime, String endTime, String payAcntName, String recAcntName) {
		try {
			payAcntName = new String(payAcntName.getBytes("iso-8859-1"), "utf-8");
			recAcntName = new String(recAcntName.getBytes("iso-8859-1"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			 e.printStackTrace();
		}
		/* 根据条件查询流水 */
		LambdaQueryWrapper<TempDraw> queryWrapper = Wrappers.lambdaQuery();
		if(StringUtils.isNotBlank(startTime)) {
			queryWrapper.ge(TempDraw::getTime, startTime);
		}
		if(StringUtils.isNotBlank(endTime)) {
			queryWrapper.le(TempDraw::getTime, endTime);
		}
		List<TempDraw> drawList = tempDrawService.list(queryWrapper);

		/* 组装有向图模型（利用Map表示有向图） */
		Map<String, String> dataMap = new HashMap<>();
		drawList.stream().forEach(tempDraw -> {
			String name1 = tempDraw.getName1();
			dataMap.put(name1,
					dataMap.get(name1) == null ? tempDraw.getName2() : dataMap.get(name1) + "," + tempDraw.getName2());
		});

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
		/* 根据条件查出流水 */
		String[] cardNoArray = StringUtils.split(StringUtils.trimToEmpty(cardNos), ',');
		List<TempDraw> drawList = tempDrawService.findDataByList(startTime, endTime, cardNoArray);

		/* 组装有向图模型（利用Map表示有向图） */
		Map<String, String> dataMap = new HashMap<>();
		drawList.stream().forEach(tempDraw -> {
			String card1 = tempDraw.getCard1();
			dataMap.put(card1,
					dataMap.get(card1) == null ? tempDraw.getCard2() : dataMap.get(card1) + "," + tempDraw.getCard2());
		});

		/* 调用算法求环路 */
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
