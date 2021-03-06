package com.ycw.fxq.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ycw.fxq.bean.TempDraw;
import com.ycw.fxq.common.response.ResponseVO;
import com.ycw.fxq.service.TempDrawService;

@RestController
@RequestMapping("/statistic-analysis")
public class StatisticAnalysisController {

    @Autowired
    private TempDrawService tempDrawService;

    @PostMapping("/amt/change")
    public ResponseVO<Map<String, Object>> showAmtChange (String caseId, String acntNo, Integer months) {
        Map<String, Object> result = new HashMap<>();
        LocalDateTime nowTime = LocalDateTime.now();
        LocalDateTime startTime = nowTime.minusMonths(months - 1L).with(TemporalAdjusters.firstDayOfMonth());
        List<TempDraw> drawList = tempDrawService.findDataByParams(caseId, acntNo, startTime, nowTime);
        String[] nameArray = new String[months];
        BigDecimal[] amtArray = new BigDecimal[months];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月");
        for (int i = 0; i < months; i++) {
            LocalDateTime bgTime = LocalDateTime.of(nowTime.toLocalDate().minusMonths(months - i - 1L).with(TemporalAdjusters.firstDayOfMonth()), LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(nowTime.toLocalDate().minusMonths(months - i - 1L).with(TemporalAdjusters.lastDayOfMonth()), LocalTime.MAX);
            nameArray[i] = startTime.format(formatter);
            amtArray[i] = drawList.stream().filter(item -> item.getTime().isAfter(bgTime) && item.getTime().isBefore(endTime)).map(TempDraw::getMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        result.put("names", nameArray);
        result.put("amts", amtArray);
        return ResponseVO.success(result);
    }

    @PostMapping("/amt/ratio")
    public ResponseVO<Map<String, Integer>> showAmtRatio (String caseId, String acntNo, LocalDateTime startTime, LocalDateTime endTime) {
        List<TempDraw> drawList = tempDrawService.findDataByParams(caseId, acntNo, startTime, endTime);
        Map<String, Integer> resultMap = new HashMap<>(16);
        resultMap.put("0k-10k", 0);
        resultMap.put("10k-20k", 0);
        resultMap.put("20k-30k", 0);
        resultMap.put("30k-40k", 0);
        resultMap.put("40k-50k", 0);
        resultMap.put("50k以上", 0);
        for (TempDraw tempDraw : drawList) {
        	BigDecimal money = tempDraw.getMoney();
			int divide = money.divide(new BigDecimal("10000"), 0, RoundingMode.DOWN).intValue();
			switch (divide) {
			case 0:
				resultMap.put("0k-10k", resultMap.get("0k-10k") + 1);
				break;
			case 1:
				resultMap.put("10k-20k", resultMap.get("0k-10k") + 1);
				break;
			case 2:
				resultMap.put("20k-30k", resultMap.get("0k-10k") + 1);
				break;
			case 3:
				resultMap.put("30k-40k", resultMap.get("0k-10k") + 1);
				break;
			case 4:
				resultMap.put("40k-50k", resultMap.get("0k-10k") + 1);
				break;
			default:
				resultMap.put("50k以上", resultMap.get("0k-10k") + 1);
				break;
			}
        }
        return ResponseVO.success(resultMap);
     }


}
