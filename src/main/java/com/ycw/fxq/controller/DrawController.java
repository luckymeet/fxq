package com.ycw.fxq.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.ycw.fxq.bean.Node;
import com.ycw.fxq.bean.TempDraw;
import com.ycw.fxq.service.impl.CommonService;
import com.ycw.fxq.service.impl.TempDrawService;

@Controller
public class DrawController {

	Logger logger = LoggerFactory.getLogger(DrawController.class);

	@Autowired
	private TempDrawService tempDrawService;

	@Autowired
	private CommonService commonService;

	private List<TempDraw> data;

	@PostConstruct
	private void init() {
		data = tempDrawService.findAllData();
	}

	@GetMapping("/index")
	public void goindex(Model model, Model model2, Model maxmoeny, Model minmoeny, HttpServletRequest request) {
		model.addAttribute("linklist", data);
		maxmoeny.addAttribute("maxmoeny", data.isEmpty() ? "" : data.get(0).getMoney());
		minmoeny.addAttribute("minmoeny", data.isEmpty() ? "" : data.get(data.size() - 1).getMoney());
		model2.addAttribute("nodelist", tempDrawService.findname());
	}

	/**
	 * 根据交易频率和交易金额过滤数据
	 *
	 * @param request
	 * @return
	 */
	@GetMapping("/filter")
	public ModelAndView filter(HttpServletRequest request) {
		List<TempDraw> linkList = getLinkList(request);// 根据交易频率和交易金额查询交易数据

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
		mv.setViewName("index");
		return mv;
	}

	/**
	 * 根据交易频率和交易金额查询交易数据
	 *
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	private List<TempDraw> getLinkList(HttpServletRequest request) {
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
		List<TempDraw> linkList = tempDrawService.filterData(params);
		return linkList;
	}

}
