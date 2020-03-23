package com.ycw.fxq.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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

//	@GetMapping("/draw/loop")
//	public ResponseVO<List<Map<String, String>>> getLoop(String startTime, String endTime, String cardNos) {
//		// 查出流水，封装Map 1->2，3，4
//		List<TempDraw> drawList = tempDrawService.findDataByList(startTime, endTime, cardNos);
//		Map<String, String> dataMap = new HashMap<>();
//		drawList.stream().forEach(tempDraw -> {
//			String card1 = tempDraw.getCard1();
//			dataMap.put(card1,
//					dataMap.get(card1) == null ? tempDraw.getCard2() : dataMap.get(card1) + "," + tempDraw.getCard2());
//		});
//		List<List<String>> resultList = new ArrayList<>();
//		drawList.stream().forEach(tempDraw -> {
//			Stack<String> previous = new Stack<>();
//			previous.push(tempDraw.getCard1());
//			commonService.findLoops(dataMap, resultList, previous, tempDraw.getCard1(), tempDraw.getCard1());
//		});
//
//		// 返回的resultList
//		List<Map<String, String>> result = resultList.stream().map(stringList -> {
//			Map<String, String> map = new HashMap<>();
//			map.put("path", stringList.stream().collect(Collectors.joining("——>")));
//			return map;
//		}).collect(Collectors.toList());
//
//		return ResponseVO.success(result);
//	}
}
