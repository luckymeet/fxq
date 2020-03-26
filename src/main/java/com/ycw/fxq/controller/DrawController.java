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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.ycw.fxq.bean.Node;
import com.ycw.fxq.bean.TempDraw;
import com.ycw.fxq.bean.TempDrawVO;
import com.ycw.fxq.common.utils.BeanHandleUtils;
import com.ycw.fxq.service.impl.CommonService;
import com.ycw.fxq.service.impl.TempDrawService;

/**
 * 拓扑图
 * @author ycw
 * @date 2020/03/24 16:42:20
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
@Controller
@RequestMapping("/draw")
public class DrawController {

	@Autowired
	private TempDrawService tempDrawService;

	@Autowired
	private CommonService commonService;

	private List<TempDrawVO> initData;

	private static final String VIEW_NAME = "topology";

	private static final String LINK_LIST = "linklist";

	private static final String NODE_LIST = "nodelist";

	@PostConstruct
	private void init() {
		initData = tempDrawService.findAllData();
	}

	@GetMapping("/topology")
	public ModelAndView topology(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		mv.addObject(LINK_LIST, initData);
		mv.addObject(NODE_LIST, tempDrawService.findname());
		mv.addObject("maxmoeny", initData.isEmpty() ? "" : initData.get(0).getMoney());
		mv.addObject("minmoeny", initData.isEmpty() ? "" : initData.get(initData.size() - 1).getMoney());
		mv.setViewName(VIEW_NAME);
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
		// 根据交易频率和交易金额查询交易数据
		List<TempDrawVO> linkList = getLinkList(request);

		/* 获取节点名称 */
		Set<String> nameSet = linkList.stream().map(TempDrawVO :: getName1).collect(Collectors.toSet());
		nameSet.addAll(linkList.stream().map(TempDrawVO :: getName2).collect(Collectors.toSet()));

		// 获取聚类后的节点列表
		List<Node> nodeList = commonService.getClusterNodeList(linkList, new ArrayList<>(nameSet));

		/* 渲染页面 */
		ModelAndView mv = new ModelAndView();
		mv.addObject(LINK_LIST, linkList);
		mv.addObject(NODE_LIST, nodeList);
		mv.addObject("maxmoeny", linkList.isEmpty() ? "" : linkList.get(0).getMoney());
		mv.addObject("minmoeny", linkList.isEmpty() ? "" : linkList.get(linkList.size() - 1).getMoney());
		mv.setViewName(VIEW_NAME);
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
		// 频率
		String frequency = request.getParameter("frequency");
		// 金额
		String amount = request.getParameter("amount");
		// 频率
		String everyDayFrequency = request.getParameter("everyDayFrequency");
		// 金额
		String everyDayAmount = request.getParameter("everyDayAmount");
		// 开始时间
		String start = request.getParameter("starttime");
		// 结束时间
		String end = request.getParameter("endtime");

		Map<String, String> params = new HashMap<>(9);
		params.put("frequency", frequency);
		params.put("amount", amount);
		params.put("everyDayFrequency", everyDayFrequency);
		params.put("everyDayAmount", everyDayAmount);
		params.put("startTime", start);
		params.put("endTime", end);
		List<TempDrawVO> linkList = tempDrawService.filterData(params);
		return linkList;
	}

	/**
	 * 查找有向图间的环路
	 *
	 * @author ycw
	 * @date 2020/03/24 16:46:21
	 * @param startTime 开始时间
	 * @param endTime   结束时间
	 * @param cardNos   账号（多个账号以逗号隔开）
	 * @return
	 */
	@GetMapping("/loop")
	public ModelAndView findLoop(String startTime, String endTime, String cardNos) {
		// 根据时间范围查询流水
		List<TempDraw> drawList = tempDrawService.findTempDrawList(startTime, endTime);

		// 组装有向图模型（利用Map表示有向图）
		Map<String, String> dataMap = commonService.createDirectedGraphByMap(drawList);

		/* 调用算法求环路 */
		String[] cardNoArray = StringUtils.split(StringUtils.trimToEmpty(cardNos), ',');
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
		Set<String> nameSet = drawList.stream().map(TempDraw :: getName1).collect(Collectors.toSet());
		nameSet.addAll(drawList.stream().map(TempDraw :: getName2).collect(Collectors.toSet()));
		List<Node> nodeList = new ArrayList<>();
		for (String name : nameSet) {
			Node node = new Node();
			node.setName(name);
			nodeList.add(node);
		}

		/* 渲染页面 */
		ModelAndView mv = new ModelAndView();
		mv.addObject(LINK_LIST, linkList);
		mv.addObject(NODE_LIST, nodeList);
		mv.addObject("maxmoeny", drawList.isEmpty() ? "" : drawList.get(0).getMoney());
		mv.addObject("minmoeny", drawList.isEmpty() ? "" : drawList.get(drawList.size() - 1).getMoney());
		mv.setViewName(VIEW_NAME);
		return mv;
	}

	/**
	 * 查找有向图间的路径
	 *
	 * @author yuminjun
	 * @date 2020/03/24 16:48:33
	 * @param startTime   开始时间
	 * @param endTime     结束时间
	 * @param payAcntName 付款账户
	 * @param recAcntName 收款账户
	 * @return
	 */
	@GetMapping("/path")
	public ModelAndView findPath(String startTime, String endTime, String payAcntName, String recAcntName) {
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
		Set<String> nameSet = drawList.stream().map(TempDraw :: getName1).collect(Collectors.toSet());
		nameSet.addAll(drawList.stream().map(TempDraw :: getName2).collect(Collectors.toSet()));
		List<Node> nodeList = new ArrayList<>();
		for (String name : nameSet) {
			Node node = new Node();
			node.setName(name);
			nodeList.add(node);
		}

		/* 渲染页面 */
		ModelAndView mv = new ModelAndView();
		mv.addObject(LINK_LIST, linkList);
		mv.addObject(NODE_LIST, nodeList);
		mv.addObject("maxmoeny", drawList.isEmpty() ? "" : drawList.get(0).getMoney());
		mv.addObject("minmoeny", drawList.isEmpty() ? "" : drawList.get(drawList.size() - 1).getMoney());
		mv.setViewName(VIEW_NAME);
		return mv;
	}

	private Set<String> getTransSet(List<List<String>> accRes) {
		Set<String> pathSet = new HashSet<>();
		for (List<String> accPath : accRes) {
			for (int i = 0; i < accPath.size() - 1; i++) {
				pathSet.add(accPath.get(i) + "-" + accPath.get(i + 1));
			}
		}
		return pathSet;
	}

}
