package com.ycw.fxq.controller;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ycw.fxq.bean.TempDrawVO;
import com.ycw.fxq.common.response.ResponseVO;
import com.ycw.fxq.service.CommonService;
import com.ycw.fxq.service.TempDrawService;

import lombok.extern.slf4j.Slf4j;

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
@Slf4j
@RestController
public class TransferRecordController {

	@Autowired
	private TempDrawService tempDrawService;

	@Autowired
	private CommonService commonService;

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
	 * @param type        类型：1-查询账号，2-查询账户
	 * @return
	 */
	@GetMapping("/draw/path/list")
	public ResponseVO<List<Map<String, String>>> findPath(Integer query) {
		List<List<String>> pathList = Collections.synchronizedList(new ArrayList<List<String>>());
		ExecutorService threadPool = DrawController.threadPool;
		String[] cardNoArray = StringUtils.split(StringUtils.trimToEmpty(DrawController.cardNos), ',');
		if (query == 1) {
			// 组装有向图模型（利用Map表示有向图）
			findCardNoPathList(pathList, threadPool, cardNoArray);
		} else if (query == 2) {
			findCardNamePathList(pathList, threadPool, cardNoArray);
		} else {
			ResponseVO.success(CollectionUtils.EMPTY_COLLECTION);
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

	private void findCardNamePathList(List<List<String>> pathList, ExecutorService threadPool, String[] cardNoArray) {
		// 组装有向图模型（利用Map表示有向图）
		Map<String, String> dataMap = commonService.createDirectedGraphByAccName(DrawController.curLinkList);
		/* 调用算法求环路 */
		int length = cardNoArray.length;
		CountDownLatch latch = new CountDownLatch(length * length);
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				String curCardName = cardNoArray[i].trim();
				Stack<String> previous = new Stack<>();
				previous.push(curCardName);
				String desCardName = cardNoArray[j].trim();
				threadPool.execute(() -> {
					commonService.findLoops(dataMap, pathList, previous, curCardName, desCardName);
					latch.countDown();
			});
			}
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			 log.error("求环路异常", e);
			 Thread.currentThread().interrupt();
		}
	}

	private void findCardNoPathList(List<List<String>> pathList, ExecutorService threadPool, String[] cardNoArray) {
		// 组装有向图模型（利用Map表示有向图）
		Map<String, String> dataMap = commonService.createDirectedGraphByAccNo(DrawController.curLinkList);
		/* 调用算法求环路 */
		List<String> acntNameList = Arrays.asList(cardNoArray);
		List<String> cardNoList = tempDrawService.findAcntNoListByAcntNameList(acntNameList);
		int size = cardNoList.size();
		CountDownLatch latch = new CountDownLatch(size * size);
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				String curCardNo = cardNoList.get(i).trim();
				Stack<String> previous = new Stack<>();
				previous.push(curCardNo);
				String desCardNo = cardNoList.get(j).trim();
				threadPool.execute(() -> {
					commonService.findLoops(dataMap, pathList, previous, curCardNo, desCardNo);
					latch.countDown();
				});
			}
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			 log.error("求环路异常", e);
			 Thread.currentThread().interrupt();
		}
	}

	private List<List<String>> findPath(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startTime, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endTime, String payAcntName, String recAcntName) {
		payAcntName = new String(payAcntName.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
		recAcntName = new String(recAcntName.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
		// 根据时间范围查询流水
		List<TempDrawVO> drawList = tempDrawService.findTempDrawList(LocalDateTime.of(startTime, LocalTime.MIN), LocalDateTime.of(endTime, LocalTime.MAX));
		// 组装有向图模型（利用Map表示有向图）
		Map<String, String> dataMap = commonService.createDirectedGraphByAccName(drawList);

		/* 调用算法求路径 */
		List<List<String>> pathList = new ArrayList<>();
		if (StringUtils.isNotBlank(payAcntName) && StringUtils.isNotBlank(recAcntName)) {
			Stack<String> previous = new Stack<>();
			previous.push(payAcntName);
			commonService.findLoops(dataMap, pathList, previous, payAcntName, recAcntName);
		}
		return pathList;
	}

	private List<List<String>> findLoop(String cardNos) {
		// 组装有向图模型（利用Map表示有向图）
		Map<String, String> dataMap = commonService.createDirectedGraphByAccNo(DrawController.curLinkList);

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
