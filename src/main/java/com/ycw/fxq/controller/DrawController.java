package com.ycw.fxq.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.ycw.fxq.service.CommonService;
import com.ycw.fxq.service.TempDrawService;

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

	public static List<TempDrawVO> initData;

	public static List<TempDrawVO> curLinkList = new ArrayList<>();

	public static String cardNos = "";

	@PostConstruct
	private void init() {
		initData = tempDrawService.findAllData();
		this.curLinkList = initData;
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
	public ModelAndView topology(HttpServletRequest request) {
		return setModelAndView(initData, tempDrawService.findname());
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
		List<TempDrawVO> linkList = getLinkList(request);
		// 获取节点名称
		List<Node> nodeList = this.getNodeListBylinkList(linkList);
		// 渲染页面
		return setModelAndView(linkList, nodeList);
	}

	/**
	 * 根据交易频率和交易金额过滤数据
	 *
	 * @author ycw
	 * @date 2020/03/24 16:46:21
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @param cardNos 账户名字符串，多个账户名以英文逗号隔开
	 * @return
	 */
	@GetMapping("/filter/account")
	public ModelAndView filterAccount(String startTime, String endTime, String cardNos) {
		this.cardNos = cardNos;
		List<String> acntNameList = Arrays.asList(StringUtils.split(cardNos, ','));
		Set<String> cardList = tempDrawService.findAcntNoListByAcntNameList(acntNameList);

		// 根据时间范围查询流水
		List<TempDrawVO> drawList = tempDrawService.findTempDrawList(startTime, endTime);
		this.curLinkList = drawList;

		/* 获取节点名称（节点为账号） */
		Set<String> nameSet = curLinkList.stream().map(TempDraw :: getCard1).collect(Collectors.toSet());
		nameSet.addAll(curLinkList.stream().map(TempDraw :: getCard2).collect(Collectors.toSet()));
		List<Node> nodeList = new ArrayList<>();
		for (String name : nameSet) {
			Node node = new Node();
			node.setName(name);
			if (cardList.contains(node.getName())) {
				node.setImgName("03.png");
			}
			nodeList.add(node);
		}

		// 渲染页面
		return setModelAndView(curLinkList, nodeList, true);
	}

	private ModelAndView setModelAndView(List<TempDrawVO> linkList, List<Node> nodeList, boolean isAccount) {
		ModelAndView mv = new ModelAndView();
		mv.addObject("linklist", linkList);
		mv.addObject("nodelist", nodeList);
		mv.addObject("isAccount", isAccount);
		mv.setViewName("topology");
		return mv;
	}

	private ModelAndView setModelAndView(List<TempDrawVO> linkList, List<Node> nodeList) {
		ModelAndView mv = new ModelAndView();
		mv.addObject("linklist", linkList);
		mv.addObject("nodelist", nodeList);
		mv.addObject("isAccount", false);
		mv.setViewName("topology");
		return mv;
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

	/**
	 * 查找有向图间的路径
	 *
	 * @author ycw
	 * @date 2020/03/24 16:46:21
	 * @param startTime 开始时间
	 * @param endTime   结束时间
	 * @param cardNos   账号（多个账号以逗号隔开）
	 * @return
	 */
	@GetMapping("/loop")
	public ModelAndView findLoop() {
		// 组装有向图模型（利用Map表示有向图）
		Map<String, String> dataMap = commonService.createDirectedGraphByAccNo(curLinkList);

		/* 调用算法求环路 */
//		String[] cardNoArray = StringUtils.split(this.cardNos, ',');
		List<String> acntNameList = Arrays.asList(StringUtils.split(cardNos, ','));
		List<String> cardNoList = new ArrayList(tempDrawService.findAcntNoListByAcntNameList(acntNameList));
		List<List<String>> loopList = new ArrayList<>();
		for (int i = 0; i < cardNoList.size(); i++) {
			for (int j = 0; j < cardNoList.size(); j++) {
				String cardNo = cardNoList.get(i).trim();
				Stack<String> previous = new Stack<>();
				previous.push(cardNo);
				commonService.findLoops(dataMap, loopList, previous, cardNo, cardNoList.get(j).trim());
			}
		}

		/* 设置节点间路径颜色 */
		Set<String> transSet = getTransSet(loopList);
		for (TempDrawVO tempDrawVO : curLinkList) {
			if (transSet.contains(tempDrawVO.getCard1() + "-" + tempDrawVO.getCard2())) {
				tempDrawVO.setColor("red");
			}
		}

		/* 获取节点名称（节点为账号） */
		Set<String> nameSet = curLinkList.stream().map(TempDraw :: getCard1).collect(Collectors.toSet());
		nameSet.addAll(curLinkList.stream().map(TempDraw :: getCard2).collect(Collectors.toSet()));
		List<Node> nodeList = new ArrayList<>();
		for (String name : nameSet) {
			Node node = new Node();
			node.setName(name);
			if (cardNoList.contains(node.getName())) {
				node.setImgName("03.png");
			}
			nodeList.add(node);
		}

		// 渲染页面
		return setModelAndView(curLinkList, nodeList, true);
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
		List<TempDrawVO> drawList = tempDrawService.findTempDrawList(startTime, endTime);
		// 组装有向图模型（利用Map表示有向图）
		Map<String, String> dataMap = commonService.createDirectedGraphByAccName(drawList);

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
		for (TempDrawVO tempDraw : drawList) {
			if (transSet.contains(tempDraw.getName1() + "-" + tempDraw.getName2())) {
				tempDraw.setColor("red");
			}
			linkList.add(tempDraw);
		}

		// 获取节点名称
		List<Node> nodeList = getNodeListBylinkList(linkList);

		// 渲染页面
		return setModelAndView(linkList, nodeList);
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

	private Set<String> getTransSet(List<List<String>> accRes) {
		Set<String> pathSet = new HashSet<>();
		for (List<String> accPath : accRes) {
			for (int i = 0; i < accPath.size() - 1; i++) {
				pathSet.add(accPath.get(i) + "-" + accPath.get(i + 1));
			}
		}
		return pathSet;
	}

	@GetMapping("/merge-account")
	public ModelAndView mergeAccount() {
		List<String> cardNoList = Arrays.asList(StringUtils.split(this.cardNos, ','));
		Map<String, Boolean> nameMap = new HashMap<>();
		for (TempDrawVO tempDrawVO : curLinkList) {
			String name1 = tempDrawVO.getName1();
			String name2 = tempDrawVO.getName2();
			if (!Boolean.TRUE.equals(nameMap.get(name1))) {
				nameMap.put(name1, cardNoList.contains(tempDrawVO.getCard1()));
			}
			if (!Boolean.TRUE.equals(nameMap.get(name2))) {
				nameMap.put(name2, cardNoList.contains(tempDrawVO.getCard2()));
			}
		}
		List<Node> nodeList = new ArrayList<>();
		for (Map.Entry<String, Boolean> entry : nameMap.entrySet()) {
			Node node = new Node();
			node.setName(entry.getKey());
			if (Boolean.TRUE.equals(entry.getValue())) {
				node.setImgName("03.png");
			}
			nodeList.add(node);
		}
		return setModelAndView(curLinkList, nodeList);
	}

}
