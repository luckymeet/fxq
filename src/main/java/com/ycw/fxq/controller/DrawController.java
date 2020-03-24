package com.ycw.fxq.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ycw.fxq.bean.Node;
import com.ycw.fxq.bean.TempDraw;
import com.ycw.fxq.bean.TempDrawVO;
import com.ycw.fxq.common.utils.BeanHandleUtils;
import com.ycw.fxq.service.impl.CommonService;
import com.ycw.fxq.service.impl.TempDrawService;

@Controller
@RequestMapping("/draw")
public class DrawController {

	Logger logger = LoggerFactory.getLogger(DrawController.class);

	@Autowired
	private TempDrawService tempDrawService;

	@Autowired
	private CommonService commonService;

	private List<TempDrawVO> data;

	@PostConstruct
	private void init() {
		data = tempDrawService.findAllData();
	}

	@GetMapping("/topology")
	public ModelAndView goindex(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		mv.addObject("linklist", data);
		mv.addObject("nodelist", tempDrawService.findname());
		mv.addObject("maxmoeny", data.isEmpty() ? "" : data.get(0).getMoney());
		mv.addObject("minmoeny", data.isEmpty() ? "" : data.get(data.size() - 1).getMoney());
		mv.setViewName("topology");
		return mv;
	}

	/**
	 * 根据交易频率和交易金额过滤数据
	 *
	 * @param request
	 * @return
	 */
	@GetMapping("/filter")
	public ModelAndView filter(HttpServletRequest request) {
		List<TempDrawVO> linkList = getLinkList(request);// 根据交易频率和交易金额查询交易数据

		/* 获取节点名称 */
		Set<String> nameSet = linkList.stream().map(item -> item.getName1()).collect(Collectors.toSet());
		nameSet.addAll(linkList.stream().map(item -> item.getName2()).collect(Collectors.toSet()));

		List<Node> nodeList = commonService.getClusterNodeList(linkList, new ArrayList<>(nameSet));// 获取聚类后的节点列表
		request.getSession().setAttribute("linkList", linkList);
		request.getSession().setAttribute("nodeList", nodeList);

		/* 渲染页面 */
		ModelAndView mv = new ModelAndView();
		mv.addObject("linklist", linkList);
		mv.addObject("nodelist", nodeList);
		mv.addObject("maxmoeny", linkList.isEmpty() ? "" : linkList.get(0).getMoney());
		mv.addObject("minmoeny", linkList.isEmpty() ? "" : linkList.get(linkList.size() - 1).getMoney());
		mv.setViewName("topology");
		return mv;
	}

	/**
	 * 根据交易频率和交易金额查询交易数据
	 *
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	private List<TempDrawVO> getLinkList(HttpServletRequest request) {
		String frequency = request.getParameter("frequency");// 频率
		String amount = request.getParameter("amount");// 金额
		String everyDayFrequency = request.getParameter("everyDayFrequency");// 频率
		String everyDayAmount = request.getParameter("everyDayAmount");// 金额
		String start = request.getParameter("starttime");
		String end = request.getParameter("endtime");

		Map<String, String> params = new HashMap<>();
		params.put("frequency", frequency);
		params.put("amount", amount);
		params.put("everyDayFrequency", everyDayFrequency);
		params.put("everyDayAmount", everyDayAmount);
		params.put("startTime", start);
		params.put("endTime", end);
		List<TempDrawVO> linkList = tempDrawService.filterData(params);
		return linkList;
	}

	@GetMapping("/loop")
	public ModelAndView getLoop(String startTime, String endTime, String cardNos) {
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
			String cardNo = cardNoArray[i].trim();
			Stack<String> previous = new Stack<>();
			previous.push(cardNo);
			commonService.findLoops(dataMap, loopList, previous, cardNo, cardNo);
		}

		/* 设置节点间路径颜色 */
		Set<String> transSet = getTransSet(loopList);
		List<TempDrawVO> linkList = new ArrayList<>();
		for (TempDraw tempDraw : drawList) {
			TempDrawVO vo = BeanHandleUtils.beanCopy(tempDraw, TempDrawVO.class);
			if (transSet.contains(tempDraw.getCard1() + "-" + tempDraw.getCard2())) {
				vo.setColor("red");
			}
			linkList.add(vo);
		}

		/* 获取节点名称 */
		Set<String> nameSet = drawList.stream().map(item -> item.getName1()).collect(Collectors.toSet());
		nameSet.addAll(drawList.stream().map(item -> item.getName2()).collect(Collectors.toSet()));
		List<Node> nodeList = new ArrayList<>();
		for (String name : nameSet) {
			Node node = new Node();
			node.setName(name);
			nodeList.add(node);
		}

		/* 渲染页面 */
		ModelAndView mv = new ModelAndView();
		mv.addObject("linklist", linkList);
		mv.addObject("nodelist", nodeList);
		mv.addObject("maxmoeny", drawList.isEmpty() ? "" : drawList.get(0).getMoney());
		mv.addObject("minmoeny", drawList.isEmpty() ? "" : drawList.get(drawList.size() - 1).getMoney());
		mv.setViewName("topology");
		return mv;
	}

	@GetMapping("/path")
	public ModelAndView getPath(String startTime, String endTime, String payAcntName, String recAcntName) {
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

		/* 设置节点间路径颜色 */
		Set<String> transSet = getTransSet(pathList);
		List<TempDrawVO> linkList = new ArrayList<>();
		for (TempDraw tempDraw : drawList) {
			TempDrawVO vo = BeanHandleUtils.beanCopy(tempDraw, TempDrawVO.class);
			if (transSet.contains(tempDraw.getName1() + "-" + tempDraw.getName2())) {
				vo.setColor("red");
			}
			linkList.add(vo);
		}

		/* 获取节点名称 */
		Set<String> nameSet = drawList.stream().map(item -> item.getName1()).collect(Collectors.toSet());
		nameSet.addAll(drawList.stream().map(item -> item.getName2()).collect(Collectors.toSet()));
		List<Node> nodeList = new ArrayList<>();
		for (String name : nameSet) {
			Node node = new Node();
			node.setName(name);
			nodeList.add(node);
		}

		/* 渲染页面 */
		ModelAndView mv = new ModelAndView();
		mv.addObject("linklist", linkList);
		mv.addObject("nodelist", nodeList);
		mv.addObject("maxmoeny", drawList.isEmpty() ? "" : drawList.get(0).getMoney());
		mv.addObject("minmoeny", drawList.isEmpty() ? "" : drawList.get(drawList.size() - 1).getMoney());
		mv.setViewName("topology");
		return mv;
	}

	public Set<String> getTransSet(List<List<String>> accRes){
		Set<String> pathSet = new HashSet<>();
		for (List<String> accPath : accRes) {
			for (int i = 0; i < accPath.size() - 1; i++) {
				pathSet.add(accPath.get(i) + "-" + accPath.get(i + 1));
			}
		}
		return pathSet;
	}

}
