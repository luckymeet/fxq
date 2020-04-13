package com.ycw.fxq.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.ycw.fxq.bean.Node;
import com.ycw.fxq.bean.TempDraw;
import com.ycw.fxq.bean.TempDrawVO;
import com.ycw.fxq.service.CommonService;
import com.ycw.fxq.service.TempDrawService;

/**
 * 可疑团伙
 * @author ycw
 * @date 2020/04/04 13:56:44
 * @version 1.00
 *
 * @record
 * <pre>
 * version  author      date          desc
 * -------------------------------------------------
 * 1.00     ycw         2020/04/04    新建
 * -------------------------------------------------
 * </pre>
 */
@Controller
@RequestMapping("/gang")
public class SuspiciousGangController {

	@Autowired
	private TempDrawService tempDrawService;

	@Autowired
	private CommonService commonService;

	public static List<TempDrawVO> curLinkList = new ArrayList<>();

	public static String cardNos = "";

	private ModelAndView setModelAndView(List<TempDrawVO> linkList, List<Node> nodeList) {
		ModelAndView mv = new ModelAndView();
		mv.addObject("linklist", linkList);
		mv.addObject("nodelist", nodeList);
		mv.addObject("isAccount", false);
		mv.setViewName("topology1");
		return mv;
	}

	/**
	 * 流水拓扑图
	 *
	 * @author ycw
	 * @date 2020/03/26 15:46:59
	 * @param request
	 * @return
	 */
	@GetMapping("/topology")
	public ModelAndView topology() {
		curLinkList = DrawController.initData;
		return setModelAndView(curLinkList, tempDrawService.findname());
	}

	/**
	 * 根据交易频率和交易金额过滤数据
	 *
	 * @author ycw
	 * @date 2020/03/24 16:46:21
	 * @param request
	 * @return
	 */
	@GetMapping("/filter/name")
	public ModelAndView filterName(HttpServletRequest request) {
		// 根据交易频率和交易金额查询交易数据
		curLinkList = getLinkList(request);
		// 获取节点名称
		List<Node> nodeList = this.getNodeListBylinkList(curLinkList);
		// 渲染页面
		return setModelAndView(curLinkList, nodeList);
	}

	private List<Node> getNodeListBylinkList(List<TempDrawVO> linkList) {
		Set<String> nameSet = linkList.stream().map(TempDraw :: getName1).collect(Collectors.toSet());
		nameSet.addAll(linkList.stream().map(TempDraw :: getName2).collect(Collectors.toSet()));
		List<Node> nodeList = new ArrayList<>();
		for (String name : nameSet) {
			Node node = new Node();
			node.setName(name);
			nodeList.add(node);
		}
		return nodeList;
	}

	/**
	 * 社区划分
	 * @author ycw
	 * @date 2020/04/04 10:57:54
	 * @return
	 */
	@GetMapping("/cluster")
	public ModelAndView cluster() {
		if (CollectionUtils.isEmpty(curLinkList)) {
			return setModelAndView(curLinkList, new ArrayList<Node>());
		}
		Set<String> nameSet = curLinkList.stream().map(TempDraw :: getName1).collect(Collectors.toSet());
		nameSet.addAll(curLinkList.stream().map(TempDraw :: getName2).collect(Collectors.toSet()));
		List<Node> nodeList = commonService.getClusterNodeList(curLinkList, new ArrayList<>(nameSet));
		return setModelAndView(curLinkList, nodeList);
	}

	/**
	 * 根据交易频率和交易金额查询交易数据
	 *
	 * @author ycw
	 * @date 2020/03/24 16:46:21
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

}
