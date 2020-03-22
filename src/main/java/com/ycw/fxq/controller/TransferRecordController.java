package com.ycw.fxq.controller;

import com.ycw.fxq.bean.TempDraw;
import com.ycw.fxq.common.response.ResponseVO;
import com.ycw.fxq.service.impl.CommonService;
import com.ycw.fxq.service.impl.TempDrawService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class TransferRecordController {
    @Autowired
    private TempDrawService tempDrawService;

    @Autowired
    private CommonService commonService;

    @PostMapping("/loop")
    public ResponseVO getLoop(String startTime, String endTime, String cardNos) {
        // 查出流水，封装Map 1->2，3，4
        List<TempDraw> drawList = tempDrawService.findDataByList(startTime,endTime,cardNos);
        Map<String,String> dataMap = new HashMap<>();
        drawList.stream()
                .forEach(tempDraw -> {
            String card1 = tempDraw.getCard1();
            dataMap.put(card1, dataMap.get(card1) == null ? tempDraw.getCard2() : dataMap.get(card1) + "," + tempDraw.getCard2());
        });
        List<List<String>> resultList = new ArrayList<>();
        drawList.stream().
                forEach(tempDraw -> commonService.findAllPaths(dataMap,resultList,new Stack<>(),tempDraw.getCard1(),tempDraw.getCard1()));

        // 返回的resultList
        List<String> result = resultList.stream()
                .map(stringList -> stringList.stream().collect(Collectors.joining(","))
        ).collect(Collectors.toList());

        return ResponseVO.success(result);
    }
}
